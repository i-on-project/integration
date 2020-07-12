package org.ionproject.integration.service.implementations

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.model.external.generic.CoreAcademicCalendar
import org.ionproject.integration.model.external.generic.CoreExamSchedule
import org.ionproject.integration.model.external.timetable.CourseTeacher
import org.ionproject.integration.model.external.timetable.School
import org.ionproject.integration.model.external.timetable.Timetable
import org.ionproject.integration.model.internal.core.CoreResult
import org.ionproject.integration.model.internal.generic.Programme
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
        val timetable = Timetable(school = School(name = "timetable"))
        val courseTeacher = CourseTeacher(school = School(name = "courseTeacher"))
        val coreAcademicCalendar = CoreAcademicCalendar(terms = listOf())
        val coreExamSchedule = CoreExamSchedule(
            school = org.ionproject.integration.model.internal.generic.School("test", acr = "test"),
            programme = Programme("test", acr = "test"),
            academicYear = "test",
            language = "test",
            exams = listOf()
        )
    }
}

@RunWith(MockitoJUnitRunner::class)
@SpringBootTest
@TestPropertySource(
    properties = [
        "ion.core-base-url = https://httpbin.org/",
        "ion.core-request-timeout-seconds = 5",
        "ion.resources-folder=src/test/resources/"
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
        val result = coreService.pushTimetable(CoreServiceTestFixtures.timetable)
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
        val result = coreService.pushCourseTeacher(CoreServiceTestFixtures.courseTeacher)
            .orThrow()

        // Assert
        assertEquals(CoreResult.SUCCESS, result)
    }

    @Test
    fun whenPushAcademicCalendar_thenReturnsSuccess() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(200)

        // Act
        val result = coreService.pushAcademicCalendar(CoreServiceTestFixtures.coreAcademicCalendar)
            .orThrow()

        // Assert
        assertEquals(CoreResult.SUCCESS, result)
    }

    @Test
    fun whenPushExamSchedule_thenReturnsSuccess() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(200)

        // Act
        val result = coreService.pushExamSchedule(CoreServiceTestFixtures.coreExamSchedule)
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
        val result = coreService.pushTimetable(CoreServiceTestFixtures.timetable)
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
        val result = coreService.pushCourseTeacher(CoreServiceTestFixtures.courseTeacher)
            .orThrow()

        // Assert
        assertEquals(CoreResult.TRY_AGAIN, result)
    }

    @Test
    fun whenPushAcademicCalendar_HttpNotFound_thenReturnsTryAgain() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(404)

        // Act
        val result = coreService.pushAcademicCalendar(CoreServiceTestFixtures.coreAcademicCalendar)
            .orThrow()

        // Assert
        assertEquals(CoreResult.TRY_AGAIN, result)
    }

    @Test
    fun whenPushExamSchedule_HttpNotFound_thenReturnsTryAgain() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(404)

        // Act
        val result = coreService.pushExamSchedule(CoreServiceTestFixtures.coreExamSchedule)
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
        val result = coreService.pushTimetable(CoreServiceTestFixtures.timetable)
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
        val result = coreService.pushCourseTeacher(CoreServiceTestFixtures.courseTeacher)
            .orThrow()

        // Assert
        assertEquals(CoreResult.TRY_AGAIN, result)
    }

    @Test
    fun whenPushAcademicCalendar_InternalServerError_thenReturnsTryAgain() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(500)

        // Act
        val result = coreService.pushAcademicCalendar(CoreServiceTestFixtures.coreAcademicCalendar)
            .orThrow()

        // Assert
        assertEquals(CoreResult.TRY_AGAIN, result)
    }

    @Test
    fun whenPushExamSchedule_InternalServerError_thenReturnsTryAgain() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(500)

        // Act
        val result = coreService.pushExamSchedule(CoreServiceTestFixtures.coreExamSchedule)
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
        val result = coreService.pushTimetable(CoreServiceTestFixtures.timetable)
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
        val result = coreService.pushCourseTeacher(CoreServiceTestFixtures.courseTeacher)
            .orThrow()

        // Assert
        assertEquals(CoreResult.EXPIRED_TOKEN, result)
    }

    @Test
    fun whenPushAcademicCalendar_NotAuthorized_thenReturnsExpiredToken() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(401)

        // Act
        val result = coreService.pushAcademicCalendar(CoreServiceTestFixtures.coreAcademicCalendar)
            .orThrow()

        // Assert
        assertEquals(CoreResult.EXPIRED_TOKEN, result)
    }

    @Test
    fun whenPushExamSchedule_NotAuthorized_thenReturnsExpiredToken() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(401)

        // Act
        val result = coreService.pushExamSchedule(CoreServiceTestFixtures.coreExamSchedule)
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
        val result = coreService.pushTimetable(CoreServiceTestFixtures.timetable)
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
        val result = coreService.pushCourseTeacher(CoreServiceTestFixtures.courseTeacher)
            .orThrow()

        // Assert
        assertEquals(CoreResult.UNRECOVERABLE_ERROR, result)
    }

    @Test
    fun whenPushAcademicCalendar_MovedPermanently_thenReturnsUnrecoverableError() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(301)

        // Act
        val result = coreService.pushAcademicCalendar(CoreServiceTestFixtures.coreAcademicCalendar)
            .orThrow()

        // Assert
        assertEquals(CoreResult.UNRECOVERABLE_ERROR, result)
    }

    @Test
    fun whenPushExamSchedule_MovedPermanently_thenReturnsUnrecoverableError() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(301)

        // Act
        val result = coreService.pushExamSchedule(CoreServiceTestFixtures.coreExamSchedule)
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
        val result = coreService.pushTimetable(CoreServiceTestFixtures.timetable)
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
        val result = coreService.pushCourseTeacher(CoreServiceTestFixtures.courseTeacher)
            .orThrow()

        // Assert
        assertEquals(CoreResult.INVALID_JSON, result)
    }

    @Test
    fun whenPushAcademicCalendar_BadRequest_thenReturnsInvalidJson() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(400)

        // Act
        val result = coreService.pushAcademicCalendar(CoreServiceTestFixtures.coreAcademicCalendar)
            .orThrow()

        // Assert
        assertEquals(CoreResult.INVALID_JSON, result)
    }

    @Test
    fun whenPushExamSchedule_BadRequest_thenReturnsInvalidJson() {
        // Arrange
        Mockito
            .`when`(httpResponse.statusCode())
            .thenReturn(400)

        // Act
        val result = coreService.pushExamSchedule(CoreServiceTestFixtures.coreExamSchedule)
            .orThrow()

        // Assert
        assertEquals(CoreResult.INVALID_JSON, result)
    }
}
