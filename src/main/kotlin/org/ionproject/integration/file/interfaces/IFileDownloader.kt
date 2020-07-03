package org.ionproject.integration.file.interfaces

import java.net.URI
import java.nio.file.Path
import org.ionproject.integration.utils.Try

interface IFileDownloader {
    fun download(uri: URI, localDestination: Path): Try<Path>
}