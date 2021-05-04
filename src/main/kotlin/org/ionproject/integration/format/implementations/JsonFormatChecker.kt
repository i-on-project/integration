package org.ionproject.integration.format.implementations

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.Type
import org.ionproject.integration.format.interfaces.IFormatChecker
import org.ionproject.integration.utils.Try

class JsonFormatChecker<T>(type: Type) : IFormatChecker {
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val jsonAdapter = moshi
        .adapter<T>(type)
        .failOnUnknown()

    override fun checkFormat(content: String): Boolean {
        return Try.ofValue(content)
            .map { c -> jsonAdapter.fromJson(c) }
            .match(
                { true },
                { false }
            )
    }
}
