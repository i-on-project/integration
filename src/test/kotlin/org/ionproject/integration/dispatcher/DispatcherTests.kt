package org.ionproject.integration.dispatcher

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File

@SpringBootTest
class DispatcherTests {

    @Autowired
    private lateinit var timetableFileWriter: TimetableFileWriter

    @Test
    fun test_jgit() {

        val REPO_NAME = "integration-data"
        val REPO_DIR = "staging/$REPO_NAME"

        File("staging").deleteRecursively()

        val repo = Git.cloneRepository()
            .setURI("http://localhost:8080/git/root/$REPO_NAME.git")
            .setDirectory(File(REPO_DIR))
            .setCredentialsProvider(UsernamePasswordCredentialsProvider("root", "root")) // LOCAL
            .call()

        val file = timetableFileWriter.write(meta, OutputFormat.YAML)

        repo.add().addFilepattern(".").call()

        repo.commit().setMessage("GANDA TESTE").call()
        repo.push().setCredentialsProvider(UsernamePasswordCredentialsProvider("root", "root")).call()
    }
}
