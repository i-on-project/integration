package org.ionproject.integration.file.implementations

import java.io.File
import java.io.FileNotFoundException
import javax.sql.DataSource
import org.ionproject.integration.hash.implementations.HashRepositoryImpl
import org.ionproject.integration.utils.orThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
        "ion.core-request-timeout-seconds = 1",
        "ion.resources-folder=src/test/resources/",
        "email.sender=alert-mailbox@domain.com",
        "spring.mail.host = localhost",
        "spring.mail.username=alert-mailbox@domain.com",
        "spring.mail.password=changeit",
        "spring.mail.port=3025",
        "spring.mail.properties.mail.smtp.auth = false",
        "spring.mail.protocol = smtp",
        "spring.mail.properties.mail.smtp.starttls.enable = false",
        "spring.mail.properties.mail.smtp.starttls.required = false"
    ]
)
internal class FileComparatorImplTest {
    @Autowired
    private lateinit var ds: DataSource

    @Test
    fun whenComparingExistingFileWithNonExistingJob_thenReturnFalse() {
        val fc = FileComparatorImpl(FileDigestImpl(), HashRepositoryImpl(ds))
        val file = File("src/test/resources/test.pdf")
        val res = fc.compare(file, "non-existing-job")
        assertFalse(res.orThrow())
    }

    @Test
    fun whenEmptyJob_thenAssertIllegalArgumentExceptionIsThrown() {
        val fc = FileComparatorImpl(FileDigestImpl(), HashRepositoryImpl(ds))
        val file = File("src/test/resources/test.pdf")
        val ex = assertThrows<IllegalArgumentException> { fc.compare(file, "").orThrow() }
        assertEquals("JobId need not be null", ex.message)
    }

    @Test
    fun whenFileIsDirectory_thenAssertIllegalArgumentExceptionIsThrown() {
        val fc = FileComparatorImpl(FileDigestImpl(), HashRepositoryImpl(ds))
        val file = File("src/test/resources")
        val ex = assertThrows<FileNotFoundException> { fc.compare(file, "job").orThrow() }
        assertTrue("${file.path} (Is a directory)" == ex.message || "${file.path} (Access is denied)" == ex.message)
    }

    @Test
    fun whenUnexistantFile_thenAssertIllegalArgumentExceptionIsThrown() {
        val fc = FileComparatorImpl(FileDigestImpl(), HashRepositoryImpl(ds))
        val file = File("src/test/resources/non-existing.pdf")
        val ex = assertThrows<IllegalArgumentException> { fc.compare(file, "job").orThrow() }
        assertEquals("File ${file.path} does not exist", ex.message)
    }

    @Test
    @Sql("insert-file-comparator-job.sql")
    fun whenHashIsTheSameAsRecorded_thenAssertTrue() {
        val fc = FileComparatorImpl(FileDigestImpl(), HashRepositoryImpl(ds))
        val file = File("src/test/resources/test.pdf")
        val actual = fc.compare(file, "file-comparator-job").orThrow()
        assertTrue(actual)
    }
    @Test
    @Sql("insert-hash-not-same.sql")
    fun whenHashIsNotTheSame_thenAssertFalse() {
        val fc = FileComparatorImpl(FileDigestImpl(), HashRepositoryImpl(ds))
        val file = File("src/test/resources/test.pdf")
        val actual = fc.compare(file, "file-comparator-job2").orThrow()
        assertFalse(actual)
    }
}
