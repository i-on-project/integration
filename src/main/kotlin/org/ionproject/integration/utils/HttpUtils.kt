package org.ionproject.integration.utils

import java.net.http.HttpClient
import org.springframework.stereotype.Component

@Component
class HttpUtils {
    val httpClientBuilder: HttpClient.Builder = HttpClient.newBuilder()
}
