package org.ionproject.integration.file.interfaces

import java.io.File
import org.ionproject.integration.utils.Try

interface FileComparator {
    fun compare(file: File, jobId: String): Try<Boolean>
}
