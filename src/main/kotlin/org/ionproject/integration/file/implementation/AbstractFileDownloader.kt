package org.ionproject.integration.file.implementation

import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Path
import org.ionproject.integration.file.`interface`.FileDownloader
import org.ionproject.integration.file.exception.ServerErrorException
import org.ionproject.integration.utils.Try

abstract class AbstractFileDownloader() :
    FileDownloader {

    override fun download(urlStr: String, localDestination: String): Try<Path> {
        if (urlStr.isEmpty() || localDestination.isEmpty()) {
            return Try.ofError(IllegalArgumentException("Parameters url and localDestination need not be empty"))
        }

        val url = URL(urlStr)

        val httpConn = Try.of(url.openConnection() as HttpURLConnection)

        val responseCode = httpConn
            .map { conn -> conn.responseCode }
            .map { code -> throwIfServerError(code) }

        val bytes = Try.map(responseCode, httpConn) { _, conn -> conn.inputStream.readBytes() }

        val isValidFormat = bytes.map { byteArray -> checkFormat(byteArray) }

        val file = isValidFormat.map { File(localDestination) }

        Try.map(file, bytes) { f: File, b: ByteArray -> f.writeBytes(b) }

        return file.map { f -> f.toPath() }
    }

    private fun throwIfServerError(code: Int): Int {
        return when (code) {
            in HttpURLConnection.HTTP_INTERNAL_ERROR..HttpURLConnection.HTTP_VERSION ->
                throw ServerErrorException("Server responded with error code $code")
            else -> code
        }
    }

    protected abstract fun checkFormat(bytes: ByteArray): Unit
}
