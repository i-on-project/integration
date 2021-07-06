package org.ionproject.integration.infrastructure.http

import java.net.URI
import java.nio.file.Path
import org.ionproject.integration.infrastructure.Try

interface IFileDownloader {
    fun download(uri: URI, localDestination: Path, timeoutInSeconds: Int = 30): Try<Path>
}
