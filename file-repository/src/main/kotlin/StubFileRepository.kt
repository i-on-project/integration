import java.io.File

class StubFileRepository : IFileRepository {
    override fun submit(file: File): SubmitResult {
        println("RECEIVED SUBMIT FOR ${file.path}")
        return SubmitResult.SUCCESS
    }
}