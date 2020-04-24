package org.ionproject.integration.file.implementation

import java.io.File
import java.net.URL
import java.nio.file.Path
import org.ionproject.integration.file.`interface`.FileDownloader
import org.ionproject.integration.file.exception.InvalidFormatException

abstract class AbstractFileDownloader(private val format: String) :
    FileDownloader {

    override fun download(url: String, localDestination: String): Path {
        // fast path
        if (url.isEmpty() || localDestination.isEmpty()) {
            throw IllegalArgumentException("Parameters url and localDestination need not be empty")
        }
        val url = URL(url)
        val bytes: ByteArray = url.readBytes()
        if (!checkFormat(bytes))
            throw InvalidFormatException("Downloaded content  was not in the $format format.")
        val file = File(localDestination)
        file.writeBytes(bytes)

        return file.toPath()
    }

    protected abstract fun checkFormat(bytes: ByteArray): Boolean
}
