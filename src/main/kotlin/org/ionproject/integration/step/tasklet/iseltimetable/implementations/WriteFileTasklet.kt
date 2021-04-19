package org.ionproject.integration.step.tasklet.iseltimetable.implementations

import java.io.File
import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.job.ISELTimetable
import org.ionproject.integration.model.external.timetable.CourseTeacher
import org.ionproject.integration.model.external.timetable.Timetable
import org.ionproject.integration.model.internal.core.CoreResult
import org.ionproject.integration.model.internal.timetable.UploadType
import org.ionproject.integration.utils.JsonUtils
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow
import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope(value = "prototype")
class WriteFileTasklet(
    private val state: ISELTimetable.State
) : Tasklet {

    private val log = LoggerFactory.getLogger(WriteFileTasklet::class.java)
    private lateinit var writeFileType: UploadType

    @Autowired
    private lateinit var appProperties: AppProperties

    fun setUploadType(type: UploadType) {
        writeFileType = type
    }

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {

        val jobContext = chunkContext.stepContext
            .stepExecution
            .jobExecution
            .executionContext

        return when (val writeResult = writeToDisk(jobContext)) {
            CoreResult.SUCCESS -> RepeatStatus.FINISHED
            else -> {
                log.error("Error Writing in Disk: $writeResult")
                RepeatStatus.FINISHED
            }
        }
    }

    private fun writeToDisk(jobContext: ExecutionContext): CoreResult {
        var index = 0
        val indexKey = "Index${writeFileType.value}"
        var result = CoreResult.SUCCESS

        if (jobContext.containsKey(indexKey)) {
            index = jobContext.getInt(indexKey)
        }

        val size = when (writeFileType) {
            UploadType.TIMETABLE -> state.timetableTeachers.timetable.size
            UploadType.TEACHERS -> state.timetableTeachers.teachers.size
            else -> throw IllegalArgumentException("Upload type $writeFileType not supported")
        }

        while (index < size) {
            result = when (writeFileType) {
                UploadType.TIMETABLE -> pushTimetable(state.timetableTeachers.timetable[index]).orThrow()
                UploadType.TEACHERS -> pushCourseTeacher(state.timetableTeachers.teachers[index]).orThrow()
                else -> throw IllegalArgumentException("Upload type $writeFileType not supported")
            }

            if (result !== CoreResult.SUCCESS) break
            index++
        }

        jobContext.putInt(indexKey, index)

        return result
    }

    fun pushTimetable(timetable: Timetable): Try<CoreResult> {
        File("${appProperties.localFileOutputFolder}/timetable.json").writeText(JsonUtils.toJson(timetable).orThrow())
        return Try.of { CoreResult.SUCCESS }
    }

    fun pushCourseTeacher(courseTeacher: CourseTeacher): Try<CoreResult> {
        File("${appProperties.localFileOutputFolder}/courseteacher.json").writeText(
            JsonUtils.toJson(courseTeacher).orThrow()
        )
        return Try.of { CoreResult.SUCCESS }
    }
}
