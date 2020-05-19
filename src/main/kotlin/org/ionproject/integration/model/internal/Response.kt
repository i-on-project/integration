package org.ionproject.integration.model.internal

/**
 * Despite being a data class, Response overrides methods equals and hashCode because
 * one of the parameters for its constructors is a ByteArray.
 * It overrides method equals to check for content equality.
 * In order to guarantee that an array with the same content also
 * produces the same hash, the hashCode method was overridden.
 **/
data class Response(val statusCode: Int, val body: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Response

        if (statusCode != other.statusCode) return false
        if (!body.contentEquals(other.body)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = statusCode
        result = 31 * result + body.contentHashCode()
        return result
    }
}
