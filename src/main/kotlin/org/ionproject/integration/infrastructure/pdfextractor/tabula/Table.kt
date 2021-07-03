package org.ionproject.integration.infrastructure.pdfextractor.tabula

data class Table(
    val extraction_method: String,
    val top: Double,
    val left: Double,
    val width: Double,
    val height: Double,
    val right: Double,
    val bottom: Double,
    val data: Array<Array<Cell>>
) {
    companion object {
        private const val HASH_PRIME_NUMBER = 31
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Table

        if (extraction_method != other.extraction_method) return false
        if (top != other.top) return false
        if (left != other.left) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (right != other.right) return false
        if (bottom != other.bottom) return false
        if (!data.contentDeepEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = extraction_method.hashCode()
        result = HASH_PRIME_NUMBER * result + top.hashCode()
        result = HASH_PRIME_NUMBER * result + left.hashCode()
        result = HASH_PRIME_NUMBER * result + width.hashCode()
        result = HASH_PRIME_NUMBER * result + height.hashCode()
        result = HASH_PRIME_NUMBER * result + right.hashCode()
        result = HASH_PRIME_NUMBER * result + bottom.hashCode()
        result = HASH_PRIME_NUMBER * result + data.contentDeepHashCode()
        return result
    }
}
