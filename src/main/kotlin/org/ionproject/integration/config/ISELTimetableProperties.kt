package org.ionproject.integration.config

import java.net.URI
import java.nio.file.Path
import javax.mail.internet.InternetAddress
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "isel-timetable")
@PropertySource("isel-timetable.properties")
class ISELTimetableProperties {
    lateinit var pdfRemoteLocation: URI
    lateinit var localFileDestination: Path
    lateinit var pdfKey: String
    lateinit var alertRecipient: InternetAddress
}
