package org.ionproject.integration.model.internal

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
        result = 31 * result + top.hashCode()
        result = 31 * result + left.hashCode()
        result = 31 * result + width.hashCode()
        result = 31 * result + height.hashCode()
        result = 31 * result + right.hashCode()
        result = 31 * result + bottom.hashCode()
        result = 31 * result + data.contentDeepHashCode()
        return result
    }
}
