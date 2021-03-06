package org.ionproject.integration.domain.timetable

interface ITimetableTeachersBuilder<T> {
    /**
     * Convert timetable raw data to business data
     */
    fun setTimetable(rawData: T)

    /**
     * Convert teachers raw data to business data
     */
    fun setTeachers(rawData: T)
}
