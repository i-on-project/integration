package org.ionproject.integration.file.implementation

import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Path
import org.ionproject.integration.file.`interface`.FileDownloader
import org.ionproject.integration.file.exception.InvalidFormatException
import org.ionproject.integration.file.exception.ServerErrorException

abstract class AbstractFileDownloader(private val format: String) :
    FileDownloader {
    private val SERVER_ERROR = 500
    override fun download(url: String, localDestination: String): Path {
        if (url.isEmpty() || localDestination.isEmpty()) {
            throw IllegalArgumentException("Parameters url and localDestination need not be empty")
        }

        val url = URL(url)

        probe(url)

        val bytes: ByteArray = url.readBytes()

        if (!checkFormat(bytes))
            throw InvalidFormatException("Downloaded content  was not in the $format format.")
        val file = File(localDestination)
        file.writeBytes(bytes)

        return file.toPath()
    }

    private fun probe(url: URL) {
        val conn = url.openConnection() as HttpURLConnection
        if (conn.responseCode >= SERVER_ERROR)
            throw ServerErrorException("Server responded with error code ${conn.responseCode}")
    }

    protected abstract fun checkFormat(bytes: ByteArray): Boolean
}
