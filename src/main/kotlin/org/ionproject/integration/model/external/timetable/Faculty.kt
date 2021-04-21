package org.ionproject.integration.model.external.timetable

import org.ionproject.integration.builder.implementations.ClassDetail

class Faculty(
    val classDetail: ClassDetail,
    var instructors: List<Instructor>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Faculty

        if (classDetail != other.classDetail) return false

        return true
    }

    override fun hashCode(): Int {
        return classDetail.hashCode()
    }
}
