package org.ionproject.integration.alert.implementations

import com.icegreen.greenmail.util.DummySSLSocketFactory
import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.GreenMailUtil
import com.icegreen.greenmail.util.ServerSetupTest
import org.apache.commons.mail.util.MimeMessageParser
import org.ionproject.integration.IOnIntegrationApplication
import org.ionproject.integration.infrastructure.alert.EmailAlertChannel
import org.ionproject.integration.infrastructure.alert.EmailConfiguration
import org.ionproject.integration.infrastructure.exceptions.AlertConfigurationException
import org.ionproject.integration.infrastructure.alert.Attachment
import org.ionproject.integration.utils.CompositeException
import org.ionproject.integration.utils.orThrow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.File
import java.security.Security
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@SpringBootTest
@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        IOnIntegrationApplication::class
    ]
)
internal class EmailAlertChannelTest {

    @Autowired
    lateinit var javaMailSender: JavaMailSenderImpl

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

    @Test
    fun whenEmailIsSuccessfullySentToOneRecipient_thenAssertMailSenderIsCalledAndReportGenerated() {
        val from = InternetAddress("alert-mailbox@domain.com")
        testSmtp.setUser(from.toUnicodeString(), "changeit")
        val to = InternetAddress("client@domain.com")
        val subject = "email-test"
        val text = "the quick brown fox jumps over the lazy dog"
        val conf = EmailConfiguration(
            arrayOf(to),
            null,
            subject,
            text
        )

        val emailAlertChannel = EmailAlertChannel(conf, javaMailSender)
        val bool = emailAlertChannel.send().orThrow()

        assertTrue(bool)
        assertionsForMailProperties(javaMailSender)

        val messages: Array<MimeMessage> = testSmtp.receivedMessages
        assertEquals(1, messages.size)
        assertEquals("email-test", messages[0].subject)
        assertTrue(GreenMailUtil.getBody(messages[0]).contains(text))
    }

    private fun assertionsForMailProperties(emailSender: JavaMailSenderImpl) {
        assertEquals("alert-mailbox@domain.com", emailSender.username)
        assertEquals("changeit", emailSender.password)
        assertEquals("localhost", emailSender.host)
        assertEquals(3025, emailSender.port)
        assertEquals("smtp", emailSender.protocol)
    }

    @Test
    fun whenEmailIsSuccessfullySentToMoreThanOneRecipient_thenAssertMailSenderIsCalledAndReportIsGenerated() {
        val from = InternetAddress("alert-mailbox@domain.com")
        testSmtp.setUser(from.toUnicodeString(), "changeit")
        val subject = "email-test2"
        val text = "the quick brown fox jumps over the lazy dog2"
        val recipients = arrayOf(InternetAddress("client@domain.com"), InternetAddress("client2@domain.com"))
        val conf = EmailConfiguration(
            recipients,
            null,
            subject,
            text
        )

        val emailAlertChannel = EmailAlertChannel(conf, javaMailSender)

        val bool = emailAlertChannel.send().orThrow()

        assertTrue(bool)
        assertionsForMailProperties(javaMailSender)
        val messages: Array<MimeMessage> = testSmtp.receivedMessages
        assertEquals(2, messages.size)
        assertEquals("email-test2", messages[0].subject)
        val actualRecipients = messages[0].allRecipients
        assertEquals(2, actualRecipients.size)
        assertTrue(actualRecipients.contentDeepEquals(recipients))
        assertTrue(GreenMailUtil.getBody(messages[0]).contains(text))
    }

    @Test
    fun whenSmtpUserIsNotTheSameAsJavaImpl_thenAssertMessageIsEqualToExpected() {
        val from = InternetAddress("different-email@domain.com")
        testSmtp.setUser(from.toUnicodeString(), "changeit")
        val subject = "email-test4"
        val text = "the quick brown fox jumps over the lazy dog4"
        val conf = EmailConfiguration(
            arrayOf(InternetAddress("client@domain.com"), InternetAddress("client2@domain.com")),
            null,
            subject,
            text
        )

        val emailAlertChannel = EmailAlertChannel(conf, javaMailSender)

        val ex = assertThrows<CompositeException> { emailAlertChannel.send().orThrow() }

        assertEquals(2, ex.exceptions.size)
        assertEquals("MailAuthenticationException", ex.exceptions[0]::class.java.simpleName)
        assertEquals(
            "Authentication failed; nested exception is javax.mail.AuthenticationFailedException: " +
                "535 5.7.8  Authentication credentials invalid\n",
            ex.exceptions[0].message
        )
        assertEquals("AlertChannelException", ex.exceptions[1]::class.java.simpleName)
        assertEquals("E-mail alert could not be sent", ex.exceptions[1].message)
    }

    @Test
    fun whenEmailSenderIsInvalid_thenAssertMessageIsEqualToExpected() {
        val from = InternetAddress("alert-mailbox@domain.com")
        testSmtp.setUser(from.toUnicodeString(), "changeit")
        val subject = "email-test3"
        val text = "the quick brown fox jumps over the lazy dog3"
        val ex = assertThrows<AlertConfigurationException> {
            EmailConfiguration(
                arrayOf(InternetAddress("client.domain"), InternetAddress("client2@domain.com")),
                null,
                subject,
                text
            )
        }
        assertEquals("There is an invalid e-mail address in the list of recipients", ex.message)
    }

    @Test
    fun whenAttachmentsAreSent_thenVerifyTheirContent() {
        val from = InternetAddress("alert-mailbox@domain.com")
        testSmtp.setUser(from.toUnicodeString(), "changeit")
        val to = InternetAddress("client@domain.com")
        val subject = "email-test5"
        val text = "the quick brown fox jumps over the lazy dog5"
        val attachmentName = "test.pdf"
        val bytesAttachment = File("src/test/resources/test.pdf").readBytes()
        val conf = EmailConfiguration(
            arrayOf(to),
            arrayOf(Attachment(attachmentName, bytesAttachment)),
            subject,
            text
        )

        val emailAlertChannel = EmailAlertChannel(conf, javaMailSender)
        val bool = emailAlertChannel.send().orThrow()

        assertTrue(bool)
        assertionsForMailProperties(javaMailSender)

        val messages: Array<MimeMessage> = testSmtp.receivedMessages
        assertEquals(1, messages.size)
        assertEquals("email-test5", messages[0].subject)
        assertTrue(GreenMailUtil.getBody(messages[0]).contains(text))

        val mimeParser: MimeMessageParser = MimeMessageParser(messages[0]).parse()
        val attachment = mimeParser.attachmentList[0]
        assertEquals(attachmentName, attachment.name)
        assertTrue(bytesAttachment.contentEquals(attachment.inputStream.readAllBytes()))
    }
}
