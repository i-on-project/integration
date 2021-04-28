package org.ionproject.integration.format.implementations

import com.squareup.moshi.Types
import org.ionproject.integration.format.exceptions.FormatCheckException
import org.ionproject.integration.format.interfaces.IRawDataFormatChecker
import org.ionproject.integration.model.internal.tabula.Table
import org.ionproject.integration.model.internal.timetable.isel.RawTimetableData
import org.ionproject.integration.utils.Try
import org.springframework.stereotype.Component

@Component
class ISELTimetableFormatChecker : IRawDataFormatChecker {

    private val jsonRootType = Types.newParameterizedType(List::class.java, Table::class.java)

    private val regexPattern = "Turma\\s?:\\s[LM][A-Za-z]+\\d{1,2}\\w+\\s+Ano Letivo\\s?:\\s?.+"

    override fun checkFormat(rawTimetableData: RawTimetableData): Try<Boolean> {

        val jsonChecker = JsonFormatChecker<List<Table>>(jsonRootType)
        val stringChecker = StringFormatChecker(regexPattern)

        val isTimetableJsonValid = Try.of { jsonChecker.checkFormat(rawTimetableData.scheduleData) }
            .flatMap { res -> mapToErrorOnFalseResult(res, "The timetable table changed its format") }

        val isInstructorsJsonValid = Try.of { jsonChecker.checkFormat(rawTimetableData.instructorData) }
            .flatMap { res -> mapToErrorOnFalseResult(res, "TODO: Instructors format error") } // TODO

        // It is assumed that if the data from the first page respects format, then all pages do
        val isStringValid = Try.of { stringChecker.checkFormat(rawTimetableData.textData.first()) }
            .flatMap { res -> mapToErrorOnFalseResult(res, "The timetable header changed its format") }

        return Try.map(
            isTimetableJsonValid,
            isStringValid,
            isInstructorsJsonValid
        ) { validations -> validations.all { it } }
    }

    private fun mapToErrorOnFalseResult(res: Boolean, errorMessage: String): Try<Boolean> {
        return if (!res) {
            Try.ofError<FormatCheckException>(FormatCheckException(errorMessage))
        } else {
            Try.ofValue(res)
        }
    }
}
