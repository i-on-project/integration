package org.ionproject.integration.format.implementation

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.ParameterizedType
import org.ionproject.integration.format.`interface`.FormatChecker
import org.ionproject.integration.model.internal.Table
import org.ionproject.integration.utils.Try
import org.ionproject.integration.utils.orElse

class JsonFormatChecker(type: ParameterizedType) : FormatChecker {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val jsonAdapter = moshi.adapter<List<Table>>(type)

    override fun checkFormat(content: String): Boolean {
        return Try.of(jsonAdapter.fromJson(content))
            .map { true }
            .orElse(false)
    }
}
