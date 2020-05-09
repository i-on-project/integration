package org.ionproject.integration.file.interfaces

import java.nio.file.Path
import org.ionproject.integration.utils.Try

interface FileDownloader {
    fun download(url: String, localDestination: String): Try<Path>
}
