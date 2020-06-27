package org.ionproject.integration.format.implementations

import com.squareup.moshi.Types
import org.ionproject.integration.format.exceptions.FormatCheckException
import org.ionproject.integration.format.interfaces.IRawDataFormatChecker
import org.ionproject.integration.model.internal.tabula.Table
import org.ionproject.integration.model.internal.timetable.isel.RawData
import org.ionproject.integration.utils.Try
import org.springframework.stereotype.Component

@Component
class ISELTimetableFormatChecker : IRawDataFormatChecker {

    private val jsonRootType = Types.newParameterizedType(List::class.java, Table::class.java)

    private val regexPattern = "\\bTurma\\b: [LM][A-Z+]\\d{1,2}[DN] \\bAno Letivo\\b: \\d{4}/\\d{2}-\\b(Verão|Inverno)\\b"

    override fun checkFormat(rawData: RawData): Try<Boolean> {

        val jsonChecker = JsonFormatChecker<List<Table>>(jsonRootType)
        val stringChecker = StringFormatChecker(regexPattern)

        val isJsonValid = Try.of { jsonChecker.checkFormat(rawData.jsonData) }
            .flatMap { res -> mapToErrorOnFalseResult(res, "The timetable table changed its format") }

        // It is assumed that if the data from the first page respects format, then all pages do
        val isStringValid = Try.of { stringChecker.checkFormat(rawData.textData.first()) }
            .flatMap { res -> mapToErrorOnFalseResult(res, "The timetable header changed its format") }

        return Try.map(isJsonValid, isStringValid) { j, s -> j.and(s) }
    }

    private fun mapToErrorOnFalseResult(res: Boolean, errorMessage: String): Try<Boolean> {
        return if (!res) {
            Try.ofError<FormatCheckException>(FormatCheckException(errorMessage))
        } else {
            Try.ofValue(res)
        }
    }
}
