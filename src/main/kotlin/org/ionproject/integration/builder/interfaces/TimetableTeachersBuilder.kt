package org.ionproject.integration.builder.interfaces

interface TimetableTeachersBuilder<T> {
    /**
     * Clears TimetableTeachers being built
     */
    fun reset()

    /**
     * Convert timetable raw data to business data
     */
    fun setTimetable(rawData: T)

    /**
     * Convert teachers raw data to business data
     */
    fun setTeachers(rawData: T)
}
