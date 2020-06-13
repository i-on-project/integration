package org.ionproject.integration.utils

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.Type

object JsonUtils {
    fun <T> fromJson(json: String, type: Type): Try<T> {
        val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val jsonAdapter = moshi
            .adapter<T>(type)
            .failOnUnknown()

        return Try.of { jsonAdapter.fromJson(json) }
            .map {
                @Suppress("UNCHECKED_CAST")
                it as T
            }
            .mapError { JsonDataException("Invalid json") }
    }
}
