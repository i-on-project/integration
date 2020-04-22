package org.ionproject.integration.file

import java.nio.file.Path

interface FileDownloader {
    fun download(url: String, localDestination: String): Path
}
