package org.ionproject.integration.file.implementations

import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import java.nio.file.Paths
import org.ionproject.integration.file.exceptions.ServerErrorException
import org.ionproject.integration.file.interfaces.IBytesFormatChecker
import org.ionproject.integration.file.interfaces.IFileDownloader
import org.ionproject.integration.model.internal.Response
import org.ionproject.integration.model.internal.generic.JobType
import org.ionproject.integration.utils.Try

class FileDownloaderImpl(private val checker: IBytesFormatChecker) :
    IFileDownloader {
    private val EMPTY_PATH = Paths.get("")

    override fun download(uri: URI, localDestination: Path, jobType: JobType?): Try<Path> {
        if (localDestination == EMPTY_PATH) {
            return Try.ofError<IllegalArgumentException>(IllegalArgumentException("Parameters url and localDestination need not be empty"))
        }

        val client = HttpClient.newHttpClient()

        val request = Try.ofValue(
            HttpRequest.newBuilder()
                .uri(uri)
                .build()
        )

        val response = request.map { r -> client.send(r, HttpResponse.BodyHandlers.ofByteArray()) }
            .map { resp -> Response(resp.statusCode(), resp.body()) }
            .flatMap { resp -> validateResponseCode(resp) }

        val file = response.map { r -> checker.checkFormat(r.body, jobType) }
            .map { localDestination.toFile() }

        println("File path: $localDestination")

        return Try.map(file, response) { f, r -> f.writeBytes(r.body); f.toPath() }
    }

    private fun validateResponseCode(response: Response): Try<Response> {
        return when (response.statusCode) {
            in HttpURLConnection.HTTP_INTERNAL_ERROR..HttpURLConnection.HTTP_VERSION ->
                return Try.ofError<ServerErrorException>(ServerErrorException("Server responded with error code ${response.statusCode}"))
            else -> Try.ofValue(response)
        }
    }
}
