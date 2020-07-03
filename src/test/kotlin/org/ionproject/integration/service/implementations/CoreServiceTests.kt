package org.ionproject.integration.service.implementations

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.model.internal.core.CoreResult
import org.ionproject.integration.model.internal.timetable.CourseTeacher
import org.ionproject.integration.model.internal.timetable.School
import org.ionproject.integration.model.internal.timetable.Timetable
import org.ionproject.integration.utils.HttpUtils
import org.ionproject.integration.utils.orThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

internal class CoreServiceTestFixtures {
    companion object {
        val timetableList = listOf(
            Timetable(school = School(name = "timetable")),
            Timetable(school = School(name = "timetable"))
        )
        val courseTeacherList = listOf(
            CourseTeacher(school = School(name = "courseTeacher")),
            CourseTeacher(school = School(name = "courseTeacher"))
        )
    }
}

@RunWith(MockitoJUnitRunner::class)
@SpringBootTest
@TestPropertySource(
    properties = [
        "ion.core-base-url = https://httpbin.org/",
        "ion.core-token = test",
        "ion.core-request-timeout-seconds = 5",
        "ion.resources-folder=src/test/resources/",
        "email.sender=alert-mailbox@domain.com",
        "spring.mail.host = localhost",
        "spring.mail.username=alert-mailbox@domain.com",
        "spring.mail.password=changeit",
        "spring.mail.port=3025",
        "spring.mail.properties.mail.smtp.auth = false",
        "spring.mail.protocol = smtp",
        "spring.mail.properties.mail.smtp.starttls.enable = false",
        "spring.mail.properties.mail.smtp.starttls.required = false"
    ]
)
class CoreServiceTests {

    @Autowired
    private lateinit var appProperties: AppProperties

    @Mock
    private lateinit var httpUtils: HttpUtils

    @Mock
    private lateinit var httpClientBuilder: HttpClient.Builder

    @Mock
    private lateinit var httpClient: HttpClient

    @Mock
    private lateinit var httpResponse: HttpResponse<String>

    private lateinit var coreService: CoreService

    @BeforeEach
    fun setUp() {
        Mockito
            .`when`(httpClient
                .send(any(HttpRequest::class.java), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(httpResponse)

        Mockito
            .`when`(httpClientBuilder.build())
            .thenReturn(httpClient)

        Mockito
            .`when`(httpClientBuilder.connectTimeout(any(Duration::class.java)))
            .thenReturn(httpClientBuilder)

        Mockito
            .`when`(httpUtils.httpClientBuilder)
            .thenReturn(httpClientBuilder)

        coreService = CoreService(httpUtils, appProperties)
    }

    @Test
    fun whenPushTimetable_thenReturnsSuccess() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(200)

        // Act
        val result = coreService.pushTimetable(CoreServiceTestFixtures.timetableList)
            .orThrow()

        // Assert
        assertEquals(CoreResult.SUCCESS, result)
    }

    @Test
    fun whenPushCourseTeacher_thenReturnsSuccess() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(200)

        // Act
        val result = coreService.pushCourseTeacher(CoreServiceTestFixtures.courseTeacherList)
            .orThrow()

        // Assert
        assertEquals(CoreResult.SUCCESS, result)
    }

    @Test
    fun whenPushTimetable_HttpNotFound_thenReturnsTryAgain() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(404)

        // Act
        val result = coreService.pushTimetable(CoreServiceTestFixtures.timetableList)
            .orThrow()

        // Assert
        assertEquals(CoreResult.TRY_AGAIN, result)
    }

    @Test
    fun whenPushCourseTeacher_HttpNotFound_thenReturnsTryAgain() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(404)

        // Act
        val result = coreService.pushCourseTeacher(CoreServiceTestFixtures.courseTeacherList)
            .orThrow()

        // Assert
        assertEquals(CoreResult.TRY_AGAIN, result)
    }

    @Test
    fun whenPushTimetable_InternalServerError_thenReturnsTryAgain() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(500)

        // Act
        val result = coreService.pushTimetable(CoreServiceTestFixtures.timetableList)
            .orThrow()

        // Assert
        assertEquals(CoreResult.TRY_AGAIN, result)
    }

    @Test
    fun whenPushCourseTeacher_InternalServerError_thenReturnsTryAgain() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(500)

        // Act
        val result = coreService.pushCourseTeacher(CoreServiceTestFixtures.courseTeacherList)
            .orThrow()

        // Assert
        assertEquals(CoreResult.TRY_AGAIN, result)
    }

    @Test
    fun whenPushTimetable_NotAuthorized_thenReturnsExpiredToken() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(401)

        // Act
        val result = coreService.pushTimetable(CoreServiceTestFixtures.timetableList)
            .orThrow()

        // Assert
        assertEquals(CoreResult.EXPIRED_TOKEN, result)
    }

    @Test
    fun whenPushCourseTeacher_NotAuthorized_thenReturnsExpiredToken() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(401)

        // Act
        val result = coreService.pushCourseTeacher(CoreServiceTestFixtures.courseTeacherList)
            .orThrow()

        // Assert
        assertEquals(CoreResult.EXPIRED_TOKEN, result)
    }

    @Test
    fun whenPushTimetable_MovedPermanently_thenReturnsUnrecoverableError() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(301)

        // Act
        val result = coreService.pushTimetable(CoreServiceTestFixtures.timetableList)
            .orThrow()

        // Assert
        assertEquals(CoreResult.UNRECOVERABLE_ERROR, result)
    }

    @Test
    fun whenPushCourseTeacher_MovedPermanently_thenReturnsUnrecoverableError() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(301)

        // Act
        val result = coreService.pushCourseTeacher(CoreServiceTestFixtures.courseTeacherList)
            .orThrow()

        // Assert
        assertEquals(CoreResult.UNRECOVERABLE_ERROR, result)
    }

    @Test
    fun whenPushTimetable_BadRequest_thenReturnsInvalidJson() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(400)

        // Act
        val result = coreService.pushTimetable(CoreServiceTestFixtures.timetableList)
            .orThrow()

        // Assert
        assertEquals(CoreResult.INVALID_JSON, result)
    }

    @Test
    fun whenPushCourseTeacher_BadRequest_thenReturnsInvalidJson() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(400)

        // Act
        val result = coreService.pushCourseTeacher(CoreServiceTestFixtures.courseTeacherList)
            .orThrow()

        // Assert
        assertEquals(CoreResult.INVALID_JSON, result)
    }
}
