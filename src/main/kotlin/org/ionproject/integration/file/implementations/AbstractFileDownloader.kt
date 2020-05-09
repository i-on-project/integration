package org.ionproject.integration.file.implementations

import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import org.ionproject.integration.file.exceptions.ServerErrorException
import org.ionproject.integration.file.interfaces.FileDownloader
import org.ionproject.integration.model.internal.Response
import org.ionproject.integration.utils.Try

abstract class AbstractFileDownloader() :
    FileDownloader {

    override fun download(url: String, localDestination: String): Try<Path> {
        if (url.isEmpty() || localDestination.isEmpty()) {
            return Try.ofError<IllegalArgumentException>(IllegalArgumentException("Parameters url and localDestination need not be empty"))
        }

        val client = Try.ofValue(HttpClient.newHttpClient())

        val request = Try.ofValue(
            HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build()
        )

        val response = Try.map(client, request) { c, r -> c.send(r, HttpResponse.BodyHandlers.ofByteArray()) }
            .map { resp -> Response(resp.statusCode(), resp.body()) }
            .flatMap { resp -> validateResponseCode(resp) }

        val file = response.map { r -> checkFormat(r.body) }
            .map { File(localDestination) }

        Try.map(file, response) { f, r -> f.writeBytes(r.body) }

        return file.map { f -> f.toPath() }
    }

    private fun validateResponseCode(response: Response): Try<Response> {
        return when (response.statusCode) {
            in HttpURLConnection.HTTP_INTERNAL_ERROR..HttpURLConnection.HTTP_VERSION ->
                return Try.ofError<ServerErrorException>(ServerErrorException("Server responded with error code ${response.statusCode}"))
            else -> Try.ofValue(response)
        }
    }

    protected abstract fun checkFormat(bytes: ByteArray): Unit
}
