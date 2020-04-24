package org.ionproject.integration.file.`interface`

import java.nio.file.Path

interface FileDownloader {
    fun download(url: String, localDestination: String): List<Result<Path>>
}
