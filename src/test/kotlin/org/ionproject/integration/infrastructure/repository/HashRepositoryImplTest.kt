package org.ionproject.integration.infrastructure.repository

import org.ionproject.integration.infrastructure.repository.hash.HashRepositoryImpl
import javax.sql.DataSource
import org.ionproject.integration.infrastructure.orThrow
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql

@SpringBootTest
internal class HashRepositoryImplTest {
    @Autowired
    private lateinit var ds: DataSource

    @Test
    fun whenFetchingHashOfNonExistingJob_ThenAssertNull() {
        val job = "job"
        val hr = HashRepositoryImpl(ds)
        val ba = hr.fetchHash(job)
        assertNull(ba)
    }

    @Test
    @Sql("../../../../../../resources/org/ionproject/integration/hash/implementations/insert-test-job.sql")
    fun whenFetchingExistingHash_thenAssertContentEquals() {
        val job = "testJob"
        val hr = HashRepositoryImpl(ds)
        val ba = hr.fetchHash(job) as ByteArray
        assertTrue(byteArrayOf(0, 0, 1, 35).contentEquals(ba))
    }

    @Test
    fun whenHashIsSuccessfullyPut_thenFetchAndAssertContentEquals() {
        val job = "anotherJob"
        val hash = byteArrayOf(1, 2, 3)
        val hr = HashRepositoryImpl(ds)
        val r = hr.putHash(job, hash)
        assertTrue(r.orThrow())
        val fetched = hr.fetchHash(job) as ByteArray
        assertTrue(fetched.contentEquals(hash))
    }

    @Test
    @Sql("../../../../../../resources/org/ionproject/integration/hash/implementations/insert-yet-another-job.sql")
    fun whenJobEntryExists_thenReplaceHashValue() {
        val job = "yetAnotherJob"
        val hash = byteArrayOf(9, 9, 9)
        val hr = HashRepositoryImpl(ds)
        val r = hr.putHash(job, hash)
        assertTrue(r.orThrow())
        val fetched = hr.fetchHash(job) as ByteArray
        assertTrue(fetched.contentEquals(hash))
    }
}
