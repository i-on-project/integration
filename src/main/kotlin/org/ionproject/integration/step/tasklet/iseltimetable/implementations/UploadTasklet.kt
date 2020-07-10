package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import org.ionproject.integration.alert.implementations.EmailAlertChannel
import org.ionproject.integration.alert.implementations.EmailAlertService
import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.model.internal.core.CoreResult
import org.ionproject.integration.model.internal.timetable.UploadType
import org.ionproject.integration.service.implementations.CoreService
import org.ionproject.integration.utils.EmailUtils
import org.ionproject.integration.utils.JobResult
import org.ionproject.integration.utils.orThrow
import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Scope
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Component

@Component
@Scope(value = "prototype")
class UploadTasklet(
    private val coreService: CoreService,
    private val appProperties: AppProperties,
    private val state: ISELTimetable.State,
    private val sender: JavaMailSenderImpl
) : Tasklet {

    private val log = LoggerFactory.getLogger(UploadTasklet::class.java)
    private lateinit var uploadType: UploadType

    fun setUploadType(type: UploadType) {
        uploadType = type
    }

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        var retries = appProperties.coreRetries
        val retriesKey = "CoreRetries${uploadType.value}"

        val jobContext = chunkContext.stepContext
            .stepExecution
            .jobExecution
            .executionContext

        if (jobContext.containsKey(retriesKey)) {
            retries = jobContext.getInt(retriesKey)
        }

        val coreResult = uploadToCore(jobContext)
        when (coreResult) {
            CoreResult.TRY_AGAIN -> {
                retries--
            }
            CoreResult.SUCCESS -> return RepeatStatus.FINISHED
            else -> {
                log.error("I-On Core replied with $coreResult")
                sendEmail(coreResult, chunkContext)
                return RepeatStatus.FINISHED
            }
        }

        jobContext.putInt(retriesKey, retries)

        return if (retries == 0) {
            log.warn("I-On Core unreachable")
            sendEmail(coreResult, chunkContext)
            RepeatStatus.FINISHED
        } else {
            RepeatStatus.CONTINUABLE
        }
    }

    private fun uploadToCore(jobContext: ExecutionContext): CoreResult {
        var index = 0
        val indexKey = "Index${uploadType.value}"
        var result = CoreResult.SUCCESS

        if (jobContext.containsKey(indexKey)) {
            index = jobContext.getInt(indexKey)
        }

        var size = when (uploadType) {
            UploadType.TIMETABLE -> state.timetableTeachers.timetable.size
            UploadType.TEACHERS -> state.timetableTeachers.teachers.size
        }

        while (index < size) {
            result = when (uploadType) {
                UploadType.TIMETABLE -> coreService.pushTimetable(state.timetableTeachers.timetable[index]).orThrow()
                UploadType.TEACHERS -> coreService.pushCourseTeacher(state.timetableTeachers.teachers[index]).orThrow()
            }

            if (result !== CoreResult.SUCCESS) break
            index++
        }

        jobContext.putInt(indexKey, index)

        return result
    }

    private fun sendEmail(coreResult: CoreResult, context: ChunkContext) {
        val alertRecipient = context.stepContext.jobParameters["alertRecipient"] as String
        val pdfRemoteLocation = context.stepContext.jobParameters["pdfRemoteLocation"] as String

        val asset = pdfRemoteLocation.substring(pdfRemoteLocation.lastIndexOf('/') + 1, pdfRemoteLocation.length)

        val message = when (coreResult) {
            CoreResult.TRY_AGAIN -> "I-On Core was unreachable with multiple retries"
            CoreResult.EXPIRED_TOKEN -> "I-On Core authentication token expired or is no longer valid"
            CoreResult.INVALID_JSON -> "Payload sent to I-On Core no longer valid"
            else -> "I-On Core unknown error"
        }

        val conf =
            EmailUtils.configure(
                "ISEL Timetable Batch Job",
                JobResult.FAILED,
                alertRecipient,
                asset,
                message
            )
        val channel = EmailAlertChannel(conf, sender)
        val alertService = EmailAlertService(channel)

        alertService.sendEmail()
    }
}
