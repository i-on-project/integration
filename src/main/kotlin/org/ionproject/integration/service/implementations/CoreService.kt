package org.ionproject.integration.service.implementations

import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.model.internal.core.CoreResult
import org.ionproject.integration.model.internal.timetable.CourseTeacher
import org.ionproject.integration.model.internal.timetable.Timetable
import org.ionproject.integration.service.interfaces.ICoreService
import org.ionproject.integration.utils.HttpUtils
import org.ionproject.integration.utils.JsonUtils
import org.ionproject.integration.utils.Try
import org.springframework.stereotype.Component

@Component
class CoreService(private val httpUtils: HttpUtils, private val appProperties: AppProperties) : ICoreService {

    override fun pushTimetable(timetableList: List<Timetable>): Try<CoreResult> {
        var timetableJson = JsonUtils.toJson(timetableList.toTypedArray())

        return sendToCore(timetableJson, URI.create("${appProperties.coreBaseUrl}/v0/insertClassSectionEvents"))
    }

    override fun pushCourseTeacher(courseTeacherList: List<CourseTeacher>): Try<CoreResult> {
        var courseTeacherJson = JsonUtils.toJson(courseTeacherList.toTypedArray())

        return sendToCore(courseTeacherJson, URI.create("${appProperties.coreBaseUrl}/v0/insertClassSectionEvents"))
    }

    private fun sendToCore(json: Try<String>, url: URI): Try<CoreResult> {
        return json
            .map { jsonString -> post(jsonString, url) }
            .map { httpResponse -> validateStatusCode(httpResponse.statusCode()) }
    }

    private fun post(jsonString: String, url: URI): HttpResponse<String> {
        var requestBuilder = HttpRequest.newBuilder()
            .uri(url)
            .POST(HttpRequest.BodyPublishers.ofString(jsonString))
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
            HttpURLConnection.HTTP_UNAUTHORIZED -> CoreResult.EXPIRED_TOKEN
            HttpURLConnection.HTTP_BAD_REQUEST -> CoreResult.INVALID_JSON
            else -> CoreResult.UNRECOVERABLE_ERROR
        }
    }
}
