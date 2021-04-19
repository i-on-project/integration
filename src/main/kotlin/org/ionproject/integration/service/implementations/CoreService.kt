package org.ionproject.integration.service.implementations

import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.model.external.generic.CoreExamSchedule
import org.ionproject.integration.model.external.generic.CoreTerm
import org.ionproject.integration.model.external.timetable.CourseTeacher
import org.ionproject.integration.model.external.timetable.Timetable
import org.ionproject.integration.model.internal.core.CoreResult
import org.ionproject.integration.service.interfaces.ICoreService
import org.ionproject.integration.utils.HttpUtils
import org.ionproject.integration.utils.JsonUtils
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orThrow
import org.springframework.stereotype.Component

@Component
class CoreService(private val httpUtils: HttpUtils, private val appProperties: AppProperties) : ICoreService {

    override fun pushTimetable(timetable: Timetable): Try<CoreResult> {
        var timetableJson = JsonUtils.toJson(timetable)

        File("/home/spring/timetable.json").writeText(timetableJson.orThrow())
        return Try.of { CoreResult.SUCCESS }
    }

    override fun pushCourseTeacher(courseTeacher: CourseTeacher): Try<CoreResult> {
        var courseTeacherJson = JsonUtils.toJson(courseTeacher)

        File("/home/spring/courseteacher.json").writeText(courseTeacherJson.orThrow())
        return Try.of { CoreResult.SUCCESS }
    }

    override fun pushCoreTerm(coreTerm: CoreTerm): Try<CoreResult> {
        var academicCalendarJson = JsonUtils.toJson(coreTerm)

        return sendToCore(academicCalendarJson, URI.create("${appProperties.coreBaseUrl}/v0/insertCalendarTerm"))
    }

    override fun pushExamSchedule(coreExamSchedule: CoreExamSchedule): Try<CoreResult> {
        var coreExamScheduleJson = JsonUtils.toJson(coreExamSchedule)

        return sendToCore(coreExamScheduleJson, URI.create("${appProperties.coreBaseUrl}/v0/insertClassSectionEvents"))
    }

    private fun sendToCore(json: Try<String>, url: URI): Try<CoreResult> {
        return json
            .map { jsonString -> put(jsonString, url) }
            .map { httpResponse ->
                validateStatusCode(httpResponse.statusCode())
            }
    }

    private fun put(jsonString: String, url: URI): HttpResponse<String> {
        var requestBuilder = HttpRequest.newBuilder()
            .uri(url)
            .PUT(HttpRequest.BodyPublishers.ofString(jsonString))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer ${appProperties.coreToken}")

        return httpUtils
            .httpClientBuilder
            .connectTimeout(Duration.ofSeconds(appProperties.coreRequestTimeoutSeconds.toLong()))
            .build()
            .send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
    }

    private fun validateStatusCode(statusCode: Int): CoreResult {
        return when (statusCode) {
            in HttpURLConnection.HTTP_OK..HttpURLConnection.HTTP_NO_CONTENT -> CoreResult.SUCCESS
            HttpURLConnection.HTTP_NOT_FOUND, HttpURLConnection.HTTP_INTERNAL_ERROR -> CoreResult.TRY_AGAIN
            HttpURLConnection.HTTP_UNAUTHORIZED, HttpURLConnection.HTTP_FORBIDDEN -> CoreResult.EXPIRED_TOKEN
            HttpURLConnection.HTTP_BAD_REQUEST -> CoreResult.INVALID_JSON
            else -> CoreResult.UNRECOVERABLE_ERROR
        }
    }
}
