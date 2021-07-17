package org.ionproject.integration.ui.config

import org.ionproject.integration.infrastructure.exception.InvalidTokenException
import org.ionproject.integration.infrastructure.exception.TokenMissingException
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.web.filter.OncePerRequestFilter
import java.util.Base64
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthFilter(private val validToken: String) : OncePerRequestFilter() {
    companion object {
        private const val HEADER = "Authorization"
        private const val PREFIX = "Bearer "
        private val SWAGGER_KEYWORDS = listOf("swagger", "api-docs")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = getToken(request)

        if (token == null || token != validToken)
            throw InvalidTokenException()

        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return SWAGGER_KEYWORDS.any(path::contains)
    }

    private fun getToken(request: HttpServletRequest): String? {
        if (!isTokenPresent(request))
            throw TokenMissingException()

        return runCatching<String> {
            val token = request.getHeader(AUTHORIZATION).substringAfter(PREFIX)
            return String(Base64.getDecoder().decode(token))
        }.getOrNull()
    }

    private fun isTokenPresent(request: HttpServletRequest): Boolean {
        return request.getHeader(HEADER)?.startsWith(PREFIX) ?: false
    }
}
