package org.ionproject.integration.service.implementations

import java.io.File
import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.model.external.generic.CoreExamSchedule
import org.ionproject.integration.model.external.generic.CoreTerm
import org.ionproject.integration.model.external.timetable.CourseTeacher
import org.ionproject.integration.model.external.timetable.Timetable
import org.ionproject.integration.model.internal.core.CoreResult
import org.ionproject.integration.service.interfaces.ICoreService
import org.ionproject.integration.utils.Directories
import org.ionproject.integration.utils.HttpUtils
import org.ionproject.integration.utils.JsonUtils
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow
import org.springframework.stereotype.Component

@Component
class CoreService(private val httpUtils: HttpUtils, private val appProperties: AppProperties) : ICoreService {

    override fun pushTimetable(timetable: Timetable): Try<CoreResult> {
        var timetableJson = JsonUtils.toJson(timetable)

        File("${Directories.LOCAL_OUTPUT_DIR}/timetable.json").writeText(timetableJson.orThrow())
        return Try.of { CoreResult.SUCCESS }
    }

    override fun pushCourseTeacher(courseTeacher: CourseTeacher): Try<CoreResult> {
        var courseTeacherJson = JsonUtils.toJson(courseTeacher)

        File("${Directories.LOCAL_OUTPUT_DIR}/courseteacher.json").writeText(courseTeacherJson.orThrow())
        return Try.of { CoreResult.SUCCESS }
    }

    override fun pushCoreTerm(coreTerm: CoreTerm): Try<CoreResult> {
        var academicCalendarJson = JsonUtils.toJson(coreTerm)

        File("${Directories.LOCAL_OUTPUT_DIR}/academiccalendar.json").writeText(academicCalendarJson.orThrow())
        return Try.of { CoreResult.SUCCESS }
    }

    override fun pushExamSchedule(coreExamSchedule: CoreExamSchedule): Try<CoreResult> {
        var coreExamScheduleJson = JsonUtils.toJson(coreExamSchedule)

        File("${Directories.LOCAL_OUTPUT_DIR}/coreExamSchedule.json").writeText(coreExamScheduleJson.orThrow())
        return Try.of { CoreResult.SUCCESS }
    }
}
