package org.ionproject.integration.infrastructure.file

import java.io.File
import org.ionproject.integration.utils.Try

interface IFileComparator {
    fun compare(file: File, jobId: String): Try<Boolean>
}
