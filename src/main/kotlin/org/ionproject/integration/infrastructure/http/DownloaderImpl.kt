package org.ionproject.integration.infrastructure.http

import org.ionproject.integration.infrastructure.Try
import org.springframework.stereotype.Service
import java.io.File
import java.lang.IllegalStateException
import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import java.time.Duration

@Service
class DownloaderImpl : IFileDownloader {
    override fun download(uri: URI, localDestination: Path, timeoutInSeconds: Int): Try<Path> {
        val response = sendRequest(uri, timeoutInSeconds)

        if (response.isError())
            throw IllegalStateException("Server responded with error code ${response.statusCode()}")

        val file = response.writeToFile(localDestination)
        return Try.of { file.toPath() }
    }

    private fun sendRequest(uri: URI, timeoutInSeconds: Int): HttpResponse<ByteArray> {
        val client = HttpClient.newHttpClient()

        val request = HttpRequest.newBuilder()
            .uri(uri)
            .timeout(Duration.ofSeconds(timeoutInSeconds.toLong()))
            .build()

        return client.send(request, HttpResponse.BodyHandlers.ofByteArray())
    }

    private fun HttpResponse<ByteArray>.isError(): Boolean =
        statusCode() in HttpURLConnection.HTTP_INTERNAL_ERROR..HttpURLConnection.HTTP_VERSION

    private fun HttpResponse<ByteArray>.writeToFile(localDestination: Path): File =
        localDestination.toFile().also { file -> file.writeBytes(body()) }
}
