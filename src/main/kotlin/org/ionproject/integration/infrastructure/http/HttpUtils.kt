package org.ionproject.integration.infrastructure.http

import java.net.http.HttpClient
import org.springframework.stereotype.Component

@Component
class HttpUtils {
    val httpClientBuilder: HttpClient.Builder = HttpClient.newBuilder()
}
