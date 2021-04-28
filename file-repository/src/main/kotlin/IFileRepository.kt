import java.io.File

interface IFileRepository {
    fun submit(file: File): SubmitResult
}

enum class SubmitResult {
    SUCCESS,
    ERROR
}
