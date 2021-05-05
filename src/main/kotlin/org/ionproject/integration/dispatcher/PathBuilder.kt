package org.ionproject.integration.dispatcher

import java.io.File

/**
 * Utility class to iteratively build file paths.
 * Instantiate with the root of your path and call the add method on each new path segment.
 * Finish by calling build() to get your final path string.
 */
class PathBuilder(root: String) {
    private val separator: String = File.separator
    private val segments = mutableListOf(root.trim())

    private var pathType: PathType = PathType.RELATIVE
    private var caseType: CaseType = CaseType.UNCHANGED

    fun add(segment: String): PathBuilder =
        this.apply {
            require(segment.isNotBlank())
            segments += segment
        }

    fun setPathType(pathType: PathType): PathBuilder = this.apply { this.pathType = pathType }
    fun setCaseType(caseType: CaseType): PathBuilder = this.apply { this.caseType = caseType }

    fun build(): File {
        fun normalize(string: String): String = string.trim().run {
            when (caseType) {
                CaseType.LOWER -> lowercase()
                CaseType.UPPER -> uppercase()
                CaseType.UNCHANGED -> this
            }
        }

        val prefix = if (pathType == PathType.ABSOLUTE) separator else ""
        val path = segments.joinToString(separator, prefix = prefix, transform = ::normalize)
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
