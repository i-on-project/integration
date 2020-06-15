package org.ionproject.integration.file.implementations

import java.io.File
import org.ionproject.integration.file.interfaces.FileComparator
import org.ionproject.integration.file.interfaces.FileDigest
import org.ionproject.integration.hash.interfaces.HashRepository
import org.ionproject.integration.utils.Try

class FileComparatorImpl(private val fd: FileDigest, private val hr: HashRepository) : FileComparator {

    override fun compare(file: File, jobId: String): Try<Boolean> {
        if (jobId.isNullOrEmpty())
            return Try.ofError<IllegalArgumentException>(IllegalArgumentException("JobId need not be null"))
        if (!file.exists()) {
            return Try.ofError<IllegalArgumentException>(IllegalArgumentException("File $file does not exist"))
        }

        val freshHash: ByteArray = fd.digest(file)
        val recordedHash: ByteArray? = hr.fetchHash(jobId)

        return if (recordedHash is ByteArray)
            Try.ofValue(freshHash.contentEquals(recordedHash))
        else Try.ofValue(false)
    }
}
