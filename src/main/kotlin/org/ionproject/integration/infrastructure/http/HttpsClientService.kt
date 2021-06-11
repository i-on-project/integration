package org.ionproject.integration.infrastructure.http

import org.ionproject.integration.file.exceptions.ServerErrorException
import org.ionproject.integration.file.interfaces.IFileDownloader
import org.ionproject.integration.model.internal.Response
import org.ionproject.integration.utils.Try
import org.springframework.stereotype.Service
import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import java.nio.file.Paths
import java.security.cert.X509Certificate
import java.time.Duration
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Service
class HttpsClientService() : IFileDownloader {

    // @Autowired
    // private lateinit var props: AppProperties

    override fun download(uri: URI, localDestination: Path): Try<Path> {
        if (isPathEmpty(localDestination))
            return Try.ofError<IllegalArgumentException>(IllegalArgumentException("Parameters url and localDestination need not be empty"))

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, Array<TrustManager>(1) { DefaultTrustManager() }, java.security.SecureRandom())

        val client = HttpClient.newBuilder()
            // .connectTimeout(Duration.ofSeconds(props.timeoutInSeconds.toLong()))
            .connectTimeout(Duration.ofSeconds(60))
            .sslContext(sslContext)
            .build()

        val request = Try.ofValue(
            HttpRequest.newBuilder()
                .uri(uri)
                .build()
        )

        val response = request.map { r -> client.send(r, HttpResponse.BodyHandlers.ofByteArray()) }
            .map { resp -> Response(resp.statusCode(), resp.body()) }
            .flatMap { resp -> validateResponseCode(resp) }

        val file = response // .map { r -> checker.checkFormat(r.body, jobType) }
            .map { localDestination.toFile() }

        return Try.map(file, response) { f, r -> f.writeBytes(r.body); f.toPath() }
    }

    private fun validateResponseCode(response: Response): Try<Response> {
        return when (response.statusCode) {
            in HttpURLConnection.HTTP_INTERNAL_ERROR..HttpURLConnection.HTTP_VERSION ->
                return Try.ofError<ServerErrorException>(ServerErrorException("Server responded with error code ${response.statusCode}"))
            else -> Try.ofValue(response)
        }
    }

    private fun isPathEmpty(path: Path): Boolean = (path == Paths.get(""))

    private class DefaultTrustManager : X509TrustManager {
        override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {}

        override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {}

        override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
    }
}
