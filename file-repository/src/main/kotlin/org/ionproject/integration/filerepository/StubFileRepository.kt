package org.ionproject.integration.filerepository

import IFileRepository
import SubmitResult
import org.springframework.stereotype.Service
import java.io.File

@Service
class StubFileRepository : IFileRepository {
    override fun submit(file: File): SubmitResult {
        println("RECEIVED SUBMIT FOR ${file.path}")
        return SubmitResult.SUCCESS
    }
}
