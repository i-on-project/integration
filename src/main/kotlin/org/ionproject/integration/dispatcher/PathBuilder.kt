package org.ionproject.integration.dispatcher

import java.io.File

/**
 * Utility class to iteratively build file paths.
 * Instantiate with the root of your path and call the add method on each new path segment.
 * Finish by calling build() to get your final path string.
 */
class PathBuilder(
    val root: String,
    val separator: String = File.separator,
    val pathType: PathType = PathType.RELATIVE,
    val caseType: CaseType = CaseType.UNCHANGED
) {
    private val segments = mutableListOf(root)

    fun add(segment: String): PathBuilder = this.apply {
        segments += segment
    }

    fun build(): File {
        val prefix = if (pathType == PathType.ABSOLUTE) separator else ""

        val path = segments.joinToString(separator, prefix = prefix).run {
            when (caseType) {
                CaseType.LOWER -> lowercase()
                CaseType.UPPER -> uppercase()
                CaseType.UNCHANGED -> this
            }
        }

        return File(path)
    }

    enum class PathType {
        RELATIVE,
        ABSOLUTE
    }

    enum class CaseType {
        LOWER,
        UPPER,
        UNCHANGED
    }
}
