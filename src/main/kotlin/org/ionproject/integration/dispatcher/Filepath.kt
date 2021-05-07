package org.ionproject.integration.dispatcher

import java.io.File

private const val EMPTY_SEGMENT_MSG = "Empty path segment"
private val INVALID_PATH_MSG = { path: String -> "Invalid path segment: $path" }
private val ILLEGAL_CHARACTERS = listOf('/', '\n', '\r', '\t', '`', '?', '*', '\\', '<', '>', '|', '\"', ':')

/**
 * Utility class to generate system appropriate file paths.
 */
class Filepath(
    segments: List<String>,
    private val pathType: PathType = PathType.RELATIVE,
    private val caseType: CaseType = CaseType.UNCHANGED,
) {
    val pathSegments = segments.map(this::sanitizeInput)

    val root = pathSegments.first()

    val path by lazy {
        pathSegments.joinToString(
            File.separator,
            prefix = pathType.prefix,
            transform = caseType::apply
        )
    }

    val asFile by lazy { File(path) }

    enum class PathType(val prefix: String) {
        RELATIVE(""),
        ABSOLUTE(File.separator)
    }

    enum class CaseType {
        LOWER {
            override fun apply(src: String): String = src.lowercase()
        },
        UPPER {
            override fun apply(src: String): String = src.uppercase()
        },
        UNCHANGED {
            override fun apply(src: String): String = src
        };

        abstract fun apply(src: String): String
    }

    private fun sanitizeInput(input: String): String =
        input.trim().also { src ->
            require(src.isNotBlank()) {
                EMPTY_SEGMENT_MSG
            }
            require(ILLEGAL_CHARACTERS.none { src.contains(it) }) {
                INVALID_PATH_MSG(input)
            }
        }

    operator fun plus(path: String): Filepath =
        Filepath(
            segments = pathSegments + path,
            caseType = caseType,
            pathType = pathType
        )
}
