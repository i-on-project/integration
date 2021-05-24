package org.ionproject.integration.model.internal.tabula

data class Cell(
    val top: Double,
    val left: Double,
    val width: Double,
    val height: Double,
    val text: String
) {
    fun isVisible(): Boolean = text.isNotBlank() && height > 0.0 && width > 0.0
}
