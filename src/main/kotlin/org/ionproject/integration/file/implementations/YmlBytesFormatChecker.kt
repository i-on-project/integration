package org.ionproject.integration.file.implementations

import org.ionproject.integration.file.exceptions.InvalidFormatException
import org.ionproject.integration.file.interfaces.IBytesFormatChecker
import org.ionproject.integration.model.internal.generic.AcademicCalendar
import org.ionproject.integration.model.internal.generic.ExamSchedule
import org.ionproject.integration.model.internal.generic.JobType
import org.ionproject.integration.utils.YamlUtils
import org.ionproject.integration.utils.orThrow

class YmlBytesFormatChecker() : IBytesFormatChecker {
    override fun checkFormat(bytes: ByteArray, jobType: JobType?) {
        when (jobType) {
            JobType.ACADEMIC_CALENDAR -> YamlUtils.fromYaml(bytes, AcademicCalendar::class.java).orThrow()
            JobType.EXAM_SCHEDULE -> YamlUtils.fromYaml(bytes, ExamSchedule::class.java).orThrow()
            else -> throw InvalidFormatException("Invalid Job Type")
        }
    }
}
