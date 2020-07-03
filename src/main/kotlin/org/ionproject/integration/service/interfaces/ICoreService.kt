package org.ionproject.integration.service.interfaces

import org.ionproject.integration.model.internal.core.CoreResult
import org.ionproject.integration.model.internal.timetable.CourseTeacher
import org.ionproject.integration.model.internal.timetable.Timetable
import org.ionproject.integration.utils.Try

interface ICoreService {
    /**
     * Push Timetable data to I-on Core
     */
    fun pushTimetable(timetableList: List<Timetable>): Try<CoreResult>

    /**
     * Push Course Teacher data to I-on Core
     */
    fun pushCourseTeacher(courseTeacherList: List<CourseTeacher>): Try<CoreResult>
}
