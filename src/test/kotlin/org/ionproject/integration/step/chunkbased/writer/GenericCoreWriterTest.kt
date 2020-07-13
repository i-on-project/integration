package org.ionproject.integration.step.chunkbased.writer

import com.icegreen.greenmail.util.DummySSLSocketFactory
import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.GreenMailUtil
import com.icegreen.greenmail.util.ServerSetupTest
import java.security.Security
import javax.mail.internet.MimeMessage
import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.config.AppProperties
import org.ionproject.integration.job.Generic
import org.ionproject.integration.model.external.generic.CoreAcademicCalendar
import org.ionproject.integration.model.external.generic.CoreExamSchedule
import org.ionproject.integration.model.external.generic.CoreTerm
import org.ionproject.integration.model.external.timetable.School
import org.ionproject.integration.model.internal.core.CoreResult
import org.ionproject.integration.model.internal.generic.Programme
import org.ionproject.integration.service.implementations.CoreService
import org.ionproject.integration.step.utils.SpringBatchTestUtils
import org.ionproject.integration.utils.Try
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        Generic::class,
        GenericCoreWriter::class,
        IOnIntegrationApplication::class
    ]
)

@TestPropertySource("classpath:application.properties", properties = ["ion.core-retries=3"])
internal class GenericCoreWriterTest {

    val school = School("School A", "A")
    val term = CoreTerm(
        school,
        "test",
        "test",
        "test",
        "test",
        listOf()
    )
    val coreAcademicCalendarItems = mutableListOf(CoreAcademicCalendar(listOf(term)))

    val schedule = CoreExamSchedule(
        org.ionproject.integration.model.internal.generic.School(school.name, school.acr),
        Programme("test", "test"),
        "test",
        "test",
        listOf()
    )
    val scheduleDidntSend = CoreExamSchedule(
        org.ionproject.integration.model.internal.generic.School(school.name, school.acr),
        Programme("test2", "tes2t"),
        "test2",
        "test2",
        listOf()
    )
    val termNotSent = CoreTerm(
        school,
        "test2",
        "test2",
        "test2",
        "test2",
        listOf()
    )
    val examScheduleItems = mutableListOf(schedule)
    val couldNotSendExamSchedule = mutableListOf(scheduleDidntSend)
    val couldNotSendAcademicCalendar = mutableListOf(CoreAcademicCalendar(listOf(termNotSent)))

    private lateinit var testSmtp: GreenMail

    @BeforeEach
    fun testSmtpInit() {
        Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory::class.java.name)
        testSmtp = GreenMail(ServerSetupTest.SMTP)
        testSmtp.start()
    }

    @AfterEach
    fun stopMailServer() {
        testSmtp.stop()
    }

    @Autowired
    private lateinit var props: AppProperties

    @Autowired
    private lateinit var sender: JavaMailSenderImpl

    @Mock
    private lateinit var coreService: CoreService

    @BeforeEach
    fun setup() {
        Mockito.`when`(coreService.pushCoreTerm(term)).thenReturn(Try.ofValue(CoreResult.SUCCESS))
        Mockito.`when`(coreService.pushExamSchedule(schedule)).thenReturn(Try.ofValue(CoreResult.SUCCESS))
        Mockito.`when`(coreService.pushExamSchedule(scheduleDidntSend)).thenReturn(Try.ofValue(CoreResult.TRY_AGAIN))
        Mockito.`when`(coreService.pushCoreTerm(termNotSent)).thenReturn(Try.ofValue(CoreResult.TRY_AGAIN))
    }

    @Test
    fun whenTypeIsAcademicCalendar_thenWriteToCoreAndAssertNoMailWasSent() {
        val se = SpringBatchTestUtils().createStepExecution()
        val writer = GenericCoreWriter(
            coreService,
            props,
            sender,
            "ACADEMIC_CALENDAR",
            "alert-mailbox@domain.com",
            "https://raw.githubusercontent.com/i-on-project/" +
                "integration-sources/master/sources/calendar/isel/calendar.yml"
        )

        writer.beforeStep(se)
        writer.write(coreAcademicCalendarItems)

        Mockito.verify(coreService, times(1)).pushCoreTerm(term)
        val messages: Array<MimeMessage> = testSmtp.receivedMessages
        assertEquals(0, messages.size)
    }

    @Test
    fun whenTypeIsExamSchedule_thenWriteToCoreAndAssertNoMailWasSent() {
        val se = SpringBatchTestUtils().createStepExecution()
        val writer = GenericCoreWriter(
            coreService,
            props,
            sender,
            "EXAM_SCHEDULE",
            "alert-mailbox@domain.com",
            "https://raw.githubusercontent.com/i-on-project/" +
                "integration-sources/master/sources/exam_schedule/isel/leic/exam_schedule.yml"
        )

        writer.beforeStep(se)
        writer.write(examScheduleItems)

        Mockito.verify(coreService, times(1)).pushExamSchedule(schedule)
        val messages: Array<MimeMessage> = testSmtp.receivedMessages
        assertEquals(0, messages.size)
    }

    @Test
    fun whenCouldNotSendExamsScheduleToCore_thenSendMail() {
        testSmtp.setUser("alert-mailbox@domain.com", "changeit")
        val se = SpringBatchTestUtils().createStepExecution()
        val writer = GenericCoreWriter(
            coreService,
            props,
            sender,
            "EXAM_SCHEDULE",
            "alert-mailbox@domain.com",
            "https://raw.githubusercontent.com/i-on-project/" +
                "integration-sources/master/sources/exam_schedule/isel/leic/exam_schedule.yml"
        )

        writer.beforeStep(se)
        writer.write(couldNotSendExamSchedule)

        Mockito.verify(coreService, times(3)).pushExamSchedule(scheduleDidntSend)
        val messages: Array<MimeMessage> = testSmtp.receivedMessages
        assertEquals(1, messages.size)
        assertEquals("i-on integration Alert - Job FAILED", messages[0].subject)
        assertTrue(
            GreenMailUtil.getBody(messages[0])
                .contains("job FAILED for file: exam_schedule.yml with message I-On Core was unreachable with multiple retries")
        )
    }
    @Test
    fun whenCouldNotSendAcademicCalendarToCore_thenSendMail() {
        testSmtp.setUser("alert-mailbox@domain.com", "changeit")
        val se = SpringBatchTestUtils().createStepExecution()
        val writer = GenericCoreWriter(
            coreService,
            props,
            sender,
            "ACADEMIC_CALENDAR",
            "alert-mailbox@domain.com",
            "https://raw.githubusercontent.com/i-on-project/" +
                "integration-sources/master/sources/calendar/isel/calendar.yml"
        )

        writer.beforeStep(se)
        writer.write(couldNotSendAcademicCalendar)

        Mockito.verify(coreService, times(3)).pushCoreTerm(termNotSent)
        val messages: Array<MimeMessage> = testSmtp.receivedMessages
        assertEquals(1, messages.size)
        assertEquals("i-on integration Alert - Job FAILED", messages[0].subject)
        assertTrue(
            GreenMailUtil.getBody(messages[0])
                .contains("job FAILED for file: calendar.yml with message I-On Core was unreachable with multiple retries")
        )
    }
}
