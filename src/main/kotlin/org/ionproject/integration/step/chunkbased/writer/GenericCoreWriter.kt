package org.ionproject.integration.step.chunkbased.writer

import org.ionproject.integration.alert.implementations.EmailAlertChannel
import org.ionproject.integration.alert.implementations.EmailAlertService
import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.model.external.generic.CoreAcademicCalendar
import org.ionproject.integration.model.external.generic.CoreExamSchedule
import org.ionproject.integration.model.external.generic.ICoreModel
import org.ionproject.integration.model.internal.core.CoreResult
import org.ionproject.integration.model.internal.generic.JobType
import org.ionproject.integration.model.internal.timetable.UploadType
import org.ionproject.integration.service.implementations.CoreService
import org.ionproject.integration.utils.EmailUtils
import org.ionproject.integration.utils.JobResult
import org.ionproject.integration.utils.orThrow
import org.slf4j.LoggerFactory
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.StepExecutionListener
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.item.ItemWriter
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Component

@StepScope
@Component("GenericCoreWriter")
class GenericCoreWriter(
    private val coreService: CoreService,
    private val appProperties: AppProperties,
    private val sender: JavaMailSenderImpl,
    @Value("#{jobParameters['jobType']}")
    private val jobType: String,
    @Value("#{jobParameters['alertRecipient']}")
    private val alertRecipient: String,
    @Value("#{jobParameters['srcRemoteLocation']}")
    private val srcRemoteLocation: String
) :
    ItemWriter<ICoreModel>, StepExecutionListener {

    private val log = LoggerFactory.getLogger(GenericCoreWriter::class.java)

    private val EXAM_SCHEDULE_LENGTH: Int = 1

    private lateinit var stepExecution: StepExecution

    private var coreResult: CoreResult = CoreResult.SUCCESS

    private var exitStatus = ExitStatus.COMPLETED

    override fun write(items: MutableList<out ICoreModel>) {
        while (tryWrite(items)) {
        }
    }

    private fun tryWrite(items: MutableList<out ICoreModel>): Boolean {
        val uploadType = mapJobTypeToUploadType(jobType)
        var retries = appProperties.coreRetries
        val retriesKey = "CoreRetries${uploadType.value}"

        val jobContext = stepExecution
            .jobExecution
            .executionContext

        if (jobContext.containsKey(retriesKey)) {
            retries = jobContext.getInt(retriesKey)
        }

        coreResult = uploadToCore(jobContext, items[0], uploadType)

        when (coreResult) {
            CoreResult.TRY_AGAIN -> {
                jobContext.putInt(retriesKey, --retries)
            }
            CoreResult.SUCCESS -> return false
            else -> {
                log.error("I-On Core replied with $coreResult")
                sendEmail(coreResult, stepExecution.jobExecution.jobInstance.jobName)
                exitStatus = ExitStatus.FAILED
                return false
            }
        }
        return if (retries == 0) {
            log.warn("I-On Core unreachable")
            sendEmail(coreResult, stepExecution.jobExecution.jobInstance.jobName)
            exitStatus = ExitStatus.FAILED
            false
        } else {
            return true
        }
    }

    private fun mapJobTypeToUploadType(jobType: String): UploadType {
        return when (val jobTypeEnum = enumValueOf<JobType>(jobType)) {
            JobType.EXAM_SCHEDULE -> UploadType.EXAM_SCHEDULE
            JobType.ACADEMIC_CALENDAR -> UploadType.ACADEMIC_CALENDAR
            else -> throw IllegalArgumentException("job type $jobTypeEnum not supported.")
        }
    }

    private fun uploadToCore(
        jobContext: ExecutionContext,
        item: ICoreModel,
        uploadType: UploadType
    ): CoreResult {
        var index = 0
        val indexKey = "Index${uploadType.value}"
        var result = CoreResult.SUCCESS

        if (jobContext.containsKey(indexKey)) {
            index = jobContext.getInt(indexKey)
        }

        var size = when (uploadType) {
            UploadType.ACADEMIC_CALENDAR -> (item as CoreAcademicCalendar).terms.size
            UploadType.EXAM_SCHEDULE -> EXAM_SCHEDULE_LENGTH
            else -> throw IllegalArgumentException("Upload Type $uploadType no supported")
        }

        while (index < size) {
            result = when (uploadType) {
                UploadType.ACADEMIC_CALENDAR -> coreService.pushCoreTerm((item as CoreAcademicCalendar).terms[index])
                    .orThrow()
                UploadType.EXAM_SCHEDULE -> coreService.pushExamSchedule((item as CoreExamSchedule)).orThrow()
                else -> throw IllegalArgumentException("Upload Type $uploadType not supported.")
            }

            if (result !== CoreResult.SUCCESS) break
            index++
        }

        jobContext.putInt(indexKey, index)

        return result
    }

    private fun sendEmail(coreResult: CoreResult, jobName: String) {

        val asset = srcRemoteLocation.substring(srcRemoteLocation.lastIndexOf('/') + 1, srcRemoteLocation.length)

        val message = when (coreResult) {
            CoreResult.TRY_AGAIN -> "I-On Core was unreachable with multiple retries"
            CoreResult.EXPIRED_TOKEN -> "I-On Core authentication token expired or is no longer valid"
            CoreResult.INVALID_JSON -> "Payload sent to I-On Core no longer valid"
            else -> "I-On Core unknown error"
        }

        val conf =
            EmailUtils.configure(
                jobName,
                JobResult.FAILED,
                alertRecipient,
                asset,
                message
            )
        val channel = EmailAlertChannel(conf, sender)
        val alertService = EmailAlertService(channel)

        alertService.sendEmail()
    }

    override fun beforeStep(stepExecution: StepExecution) {
        this.stepExecution = stepExecution
    }

    override fun afterStep(stepExecution: StepExecution): ExitStatus? {
        return exitStatus
    }
}
