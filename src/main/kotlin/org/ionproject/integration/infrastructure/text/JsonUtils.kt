package org.ionproject.integration.infrastructure.text

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.ionproject.integration.utils.Try
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

    fun <T : Any> toJson(t: T): Try<String> {
        val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val jsonAdapter = moshi
            .adapter<T>(t::class.java)
            .failOnUnknown()

        return Try.of { jsonAdapter.toJson(t) }
            .mapError { JsonDataException("Could not stringify value $t") }
    }
}
