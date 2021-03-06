package org.ionproject.integration.infrastructure.repository.hash

import org.ionproject.integration.infrastructure.Try

interface IHashRepository {
    fun fetchHash(jobId: String): ByteArray?
    fun putHash(jobId: String, hash: ByteArray): Try<Boolean>
}
