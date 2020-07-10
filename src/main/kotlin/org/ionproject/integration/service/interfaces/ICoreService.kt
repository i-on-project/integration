package org.ionproject.integration.service.interfaces

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
}
