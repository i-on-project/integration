package org.ionproject.integration

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(
    properties = [
        "spring.datasource.url = jdbc:h2:mem:testdb",
        "spring.datasource.driverClassName = org.h2.Driver",
        "spring.datasource.username = sa",
        "spring.datasource.password = ",
        "ion.core-base-url = test",
        "ion.core-token = test",
        "ion.core-request-timeout-seconds = 1",
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
class IOnIntegrationApplicationTests {

    @Test
    fun contextLoads() {
    }
}
