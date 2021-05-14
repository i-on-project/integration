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
import org.slf4j.LoggerFactory

class FileDownloaderImpl(private val checker: IBytesFormatChecker) :
    IFileDownloader {
    private val EMPTY_PATH = Paths.get("")

    private val log = LoggerFactory.getLogger(FileDownloaderImpl::class.java)

    override fun download(uri: URI, localDestination: Path, jobType: JobType?): Try<Path> {
        if (localDestination == EMPTY_PATH) {
            return Try.ofError<IllegalArgumentException>(IllegalArgumentException("Parameters url and localDestination need not be empty"))
        }

        val client = HttpClient.newHttpClient()

        val request = HttpRequest.newBuilder()
            .uri(uri)
            .build()

        val response = kotlin.runCatching {
            val httpResponse = client.send(request, HttpResponse.BodyHandlers.ofByteArray())
            val response = Response(httpResponse.statusCode(), httpResponse.body())
            validateResponseCode(response)
        }.onFailure {
            log.error("Error downloading $uri: $it -> ${it.message}")
        }.getOrNull()

        val file = localDestination.toFile()

        return Try.of {
            if (response == null) throw IllegalArgumentException("Response is null")

            file.writeBytes(response.body)
            file.toPath()
        }
    }

    private fun validateResponseCode(response: Response): Response {
        return when (response.statusCode) {
            in HttpURLConnection.HTTP_INTERNAL_ERROR..HttpURLConnection.HTTP_VERSION ->
                throw ServerErrorException("Server responded with error code ${response.statusCode}")
            else -> response
        }
    }
}
