package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.model.internal.core.CoreResult
import org.ionproject.integration.model.internal.timetable.UploadType
import org.ionproject.integration.service.implementations.CoreService
import org.ionproject.integration.utils.orThrow
import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope(value = "prototype")
class UploadTasklet(private val coreService: CoreService, private val appProperties: AppProperties, private val state: ISELTimetable.State) : Tasklet {

    private val contextKey = "CoreRetries"
    private val log = LoggerFactory.getLogger(UploadTasklet::class.java)
    private lateinit var uploadType: UploadType

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

        when (val coreResult = uploadToCore()) {
            CoreResult.TRY_AGAIN -> {
                retries--
            }
            else -> {
                return RepeatStatus.FINISHED
            }
        }

        jobContext.putInt(contextKey, retries)

        return if (retries == 0) {
            RepeatStatus.FINISHED
        } else {
            RepeatStatus.CONTINUABLE
        }
    }

    private fun uploadToCore() = when (uploadType) {
        UploadType.TIMETABLE -> coreService.pushTimetable(state.timetableTeachers.timetable).orThrow()
        UploadType.TEACHERS -> coreService.pushCourseTeacher(state.timetableTeachers.teachers).orThrow()
    }
}
