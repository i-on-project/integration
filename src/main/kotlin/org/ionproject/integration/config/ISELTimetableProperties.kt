package org.ionproject.integration.config

import java.net.URI
import java.nio.file.Path
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component

@Component
@Configuration
@ConfigurationProperties(prefix = "isel-timetable")
@PropertySource("isel-timetable.properties")
class ISELTimetableProperties {
    lateinit var pdfRemoteLocation: URI
    lateinit var localFileDestination: Path
    lateinit var pdfKey: String
}
