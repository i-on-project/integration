package org.ionproject.integration.infrastructure

import org.ionproject.integration.utils.Try

interface IHashRepository {
    fun fetchHash(jobId: String): ByteArray?
    fun putHash(jobId: String, hash: ByteArray): Try<Boolean>
}
