package org.ionproject.integration.infrastructure.repository.hash

import javax.sql.DataSource
import org.ionproject.integration.infrastructure.Try
import org.ionproject.integration.infrastructure.repository.GET_HASH_QUERY
import org.ionproject.integration.infrastructure.repository.INSERT_HASH_QUERY
import org.ionproject.integration.infrastructure.repository.UPDATE_HASH_QUERY
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class HashRepositoryImpl(private val ds: DataSource) : IHashRepository {

    override fun fetchHash(jobId: String): ByteArray? {
        return JdbcTemplate(ds)
            .query(
                { conn -> conn.prepareStatement(GET_HASH_QUERY) },
                { pss -> pss.setString(1, jobId) },
                { rss -> if (rss.next()) rss.getBytes(1) else null }
            )
    }

    override fun putHash(jobId: String, hash: ByteArray): Try<Boolean> {
        return Try.of {
            if (fetchHash(jobId) == null) {
                insert(jobId, hash)
            } else {
                update(jobId, hash)
            }
        }.map { nAffected -> nAffected == 1 }
    }

    private fun insert(jobId: String, hash: ByteArray): Int {
        val query = INSERT_HASH_QUERY
        return JdbcTemplate(ds).update(
            query, jobId, hash
        )
    }

    private fun update(jobId: String, hash: ByteArray): Int {
        return JdbcTemplate(ds).update(UPDATE_HASH_QUERY, hash, jobId)
    }
}
