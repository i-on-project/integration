package org.ionproject.integration.file.implementation

import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Path
import org.ionproject.integration.file.`interface`.FileDownloader
import org.ionproject.integration.file.exception.ServerErrorException
import org.ionproject.integration.model.internal.Response
import org.ionproject.integration.utils.Try
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

abstract class AbstractFileDownloader() :
    FileDownloader {

    override fun download(urlStr: String, localDestination: String): Try<Path> {
        if (urlStr.isEmpty() || localDestination.isEmpty()) {
            return Try.ofError(IllegalArgumentException("Parameters url and localDestination need not be empty"))
        }

        val client = Try.of(HttpClient.newHttpClient())

        val request = Try.of(
            HttpRequest.newBuilder()
            .uri(URI.create(urlStr))
            .build())

        val response = Try.map(client, request)
        {c, r -> c.send(r, HttpResponse.BodyHandlers.ofByteArray())}
            .map { resp -> Response(resp.statusCode(),resp.body()) }
            .flatMap { resp -> validateResponseCode(resp) }

        val file = response.map { r -> checkFormat(r.body) }
            .map { File(localDestination) }

        Try.map(file, response) { f, r -> f.writeBytes(r.body)}

        return file.map { f -> f.toPath() }
    }

    private fun validateResponseCode(response : Response): Try<Response> {
        return when (response.statusCode) {
            in HttpURLConnection.HTTP_INTERNAL_ERROR..HttpURLConnection.HTTP_VERSION ->
                return Try.ofError(ServerErrorException("Server responded with error code ${response.statusCode}"))
            else -> Try.of(response)
        }
    }

    protected abstract fun checkFormat(bytes: ByteArray): Unit
}
