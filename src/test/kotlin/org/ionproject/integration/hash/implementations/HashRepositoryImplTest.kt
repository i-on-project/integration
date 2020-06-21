package org.ionproject.integration.hash.implementations

import javax.sql.DataSource
import org.ionproject.integration.utils.orThrow
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql

@SpringBootTest
@TestPropertySource(
    properties = [
        "spring.datasource.url = jdbc:h2:mem:testdb",
        "spring.datasource.driverClassName = org.h2.Driver",
        "spring.datasource.username = sa",
        "spring.datasource.password = ",
        "ion.core-base-url = test",
        "ion.core-token = test",
        "ion.core-request-timeout-seconds = 1"
    ]
)
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
    @Sql("insert-test-job.sql")
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
    @Sql("insert-yet-another-job.sql")
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
