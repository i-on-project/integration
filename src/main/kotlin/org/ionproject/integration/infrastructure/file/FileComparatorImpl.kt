package org.ionproject.integration.infrastructure.file

import org.ionproject.integration.infrastructure.repository.hash.IHashRepository
import java.io.File
import org.ionproject.integration.infrastructure.Try

class FileComparatorImpl(private val fd: IFileDigest, private val hr: IHashRepository) : IFileComparator {

    override fun compare(file: File, jobId: String): Try<Boolean> {
        if (jobId.isEmpty())
            return Try.ofError<IllegalArgumentException>(IllegalArgumentException("JobId need not be null"))
        if (!file.exists()) {
            return Try.ofError<IllegalArgumentException>(IllegalArgumentException("File $file does not exist"))
        }
        return Try.of {
            val freshHash: ByteArray = fd.digest(file)
            val recordedHash: ByteArray? = hr.fetchHash(jobId)
            if (recordedHash is ByteArray)
                freshHash.contentEquals(recordedHash)
            else false
        }
    }
}
