package org.ionproject.integration.format.implementation

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.Type
import org.ionproject.integration.format.`interface`.FormatChecker
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orElse

class JsonFormatChecker<T>(type: Type) : FormatChecker {
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val jsonAdapter = moshi
        .adapter<T>(type)
        .failOnUnknown()

    override fun checkFormat(content: String): Boolean {
        return Try.of(content)
            .map { c -> jsonAdapter.fromJson(c) }
            .map { true }
            .orElse(false)
    }
}
