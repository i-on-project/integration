package org.ionproject.integration.infrastructure.alert

data class Attachment(val fileName: String, val byteArray: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Attachment

        if (fileName != other.fileName) return false
        if (!byteArray.contentEquals(other.byteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileName.hashCode()
        result = 31 * result + byteArray.contentHashCode()
        return result
    }
}
