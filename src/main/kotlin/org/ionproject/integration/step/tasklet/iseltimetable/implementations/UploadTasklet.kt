package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import org.ionproject.integration.alert.implementations.EmailAlertService
import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.model.internal.core.CoreResult
import org.ionproject.integration.model.internal.timetable.UploadType
import org.ionproject.integration.service.implementations.CoreService
import org.ionproject.integration.utils.orThrow
import org.slf4j.LoggerFactory
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.StepExecutionListener
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
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
) : Tasklet, StepExecutionListener {

    private val contextKey = "CoreRetries"
    private val log = LoggerFactory.getLogger(UploadTasklet::class.java)
    private lateinit var uploadType: UploadType
    private lateinit var coreResult: CoreResult

    fun setUploadType(type: UploadType) {
        uploadType = type
    }

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        var retries = appProperties.coreRetries

        val jobContext = chunkContext.stepContext
            .stepExecution
            .jobExecution
            .executionContext

        if (jobContext.containsKey(contextKey)) {
            retries = jobContext.getInt(contextKey)
        }

        coreResult = uploadToCore()
        when (coreResult) {
            CoreResult.TRY_AGAIN -> {
                retries--
            }
            else -> {
                log.error("I-On Core replied with $coreResult")
                sendEmail(coreResult, chunkContext)
                return RepeatStatus.FINISHED
            }
        }

        jobContext.putInt(contextKey, retries)

        return if (retries == 0) {
            log.warn("I-On Core unreachable")
            sendEmail(coreResult, chunkContext)
            RepeatStatus.FINISHED
        } else {
            RepeatStatus.CONTINUABLE
        }
    }

    override fun beforeStep(stepExecution: StepExecution) {
    }

    override fun afterStep(stepExecution: StepExecution): ExitStatus {
        if (coreResult !== CoreResult.SUCCESS) {
            return ExitStatus.STOPPED
        }

        return ExitStatus.COMPLETED
    }

    private fun uploadToCore() = when (uploadType) {
        UploadType.TIMETABLE -> coreService.pushTimetable(state.timetableTeachers.timetable).orThrow()
        UploadType.TEACHERS -> coreService.pushCourseTeacher(state.timetableTeachers.teachers).orThrow()
    }

    private fun sendEmail(coreResult: CoreResult, context: ChunkContext) {
        val alertRecipient = context.stepContext.jobParameters["alertRecipient"] as String
        val pdfRemoteLocation = context.stepContext.jobParameters["pdfRemoteLocation"] as String

        val asset = pdfRemoteLocation.substring(pdfRemoteLocation.lastIndexOf('/') + 1, pdfRemoteLocation.length)
        val alertService = EmailAlertService("ISEL Timetable Batch Job", alertRecipient, asset, sender)

        val message = when (coreResult) {
            CoreResult.TRY_AGAIN -> "I-On Core was unreachable with multiple retries"
            CoreResult.EXPIRED_TOKEN -> "I-On Core authentication token expired or is no longer valid"
            CoreResult.INVALID_JSON -> "Payload sent to I-On Core no longer valid"
            else -> "I-On Core unknown error"
        }

        alertService.sendFailureEmail("$message when trying to send ${uploadType.value} data")
    }
}
