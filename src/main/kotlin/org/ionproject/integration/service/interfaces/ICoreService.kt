package org.ionproject.integration.service.interfaces

import org.ionproject.integration.model.external.generic.CoreAcademicCalendar
import org.ionproject.integration.model.external.generic.CoreExamSchedule
import org.ionproject.integration.model.external.timetable.CourseTeacher
import org.ionproject.integration.model.external.timetable.Timetable
import org.ionproject.integration.model.internal.core.CoreResult
import org.ionproject.integration.utils.Try

interface ICoreService {
    /**
     * Push Timetable data to I-on Core
     */
    fun pushTimetable(timetable: Timetable): Try<CoreResult>

    /**
     * Push Course Teacher data to I-on Core
     */
    fun pushCourseTeacher(courseTeacher: CourseTeacher): Try<CoreResult>

    /**
     * Push Academic Calendar data to I-on Core
     */
    fun pushAcademicCalendar(coreAcademicCalendar: CoreAcademicCalendar): Try<CoreResult>

    /**
     * Push Exam Schedule data to I-on Core
     */
    fun pushExamSchedule(coreExamSchedule: CoreExamSchedule): Try<CoreResult>
}
