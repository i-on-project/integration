package org.ionproject.integration.format.implementation

import com.squareup.moshi.Types
import org.ionproject.integration.format.exception.FormatCheckException
import org.ionproject.integration.model.internal.DynamicObject
import org.ionproject.integration.model.internal.Table
import org.ionproject.integration.utils.Try

class ISELTimetableFormatChecker {

    private val jsonRootType = Types.newParameterizedType(List::class.java, Table::class.java)

    private val regexPattern = "\\bTurma\\b: [LM][A-Z+]\\d{2}[DN] \\bAno Letivo\\b: \\d{4}/\\d{2}-\\b(Verão|Inverno)\\b"

    fun checkFormat(dynamicObject: DynamicObject): Try<Boolean> {

        val jsonChecker = JsonFormatChecker(jsonRootType)
        val stringChecker = StringFormatChecker(regexPattern)

        val isJsonValid = Try.of(jsonChecker.checkFormat(dynamicObject.jsonData))
            .flatMap { res -> mapToErrorOnFalseResult(res, "The timetable table changed its format") }

        val isStringValid = Try.of(stringChecker.checkFormat(dynamicObject.textData.first()))
            .flatMap { res -> mapToErrorOnFalseResult(res, "The timetable header changed its format") }

        return Try.map(isJsonValid, isStringValid) { j, s -> j.and(s) }
    }
    private fun mapToErrorOnFalseResult(res: Boolean, errorMessage: String): Try<Boolean> {
        return if (!res) {
            Try.ofError(FormatCheckException(errorMessage))
        } else {
            Try.of(res)
        }
    }
}
