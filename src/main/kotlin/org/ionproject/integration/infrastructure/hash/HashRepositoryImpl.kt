package org.ionproject.integration.infrastructure.hash

import javax.sql.DataSource
import org.ionproject.integration.utils.Try
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class HashRepositoryImpl(private val ds: DataSource) : IHashRepository {

    override fun fetchHash(jobId: String): ByteArray? {
        val query = "SELECT hash from filehashes where jobId = ?"

        return JdbcTemplate(ds)
            .query(
                { conn -> conn.prepareStatement(query) },
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
        val query = "insert into filehashes values(?,?)"
        return JdbcTemplate(ds).update(
            query, jobId, hash
        )
    }

    private fun update(jobId: String, hash: ByteArray): Int {
        val query = "update filehashes set hash=? where jobId = ?"
        return JdbcTemplate(ds).update(
            query, hash, jobId
        )
    }
}
