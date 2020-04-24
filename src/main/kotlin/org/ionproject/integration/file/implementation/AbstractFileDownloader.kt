package org.ionproject.integration.file.implementation

import java.io.File
import java.net.URL
import java.nio.file.Path
import org.ionproject.integration.file.`interface`.FileDownloader
import org.ionproject.integration.file.exception.InvalidFormatException
import java.io.IOException

abstract class AbstractFileDownloader(private val format: String) :
    FileDownloader {

    override fun download(url: String, localDestination: String): List<Result<Path>> {
        val res = mutableListOf<Result<Path>>()
        // fast path
        if(url.isEmpty()){
            res.add(Result.failure(IOException()))
            return res
        }
        val url = URL(url)
        val bytes: ByteArray = url.readBytes()
        if (!checkFormat(bytes))
            res.add(Result.failure(InvalidFormatException("Downloaded content  was not in the $format format.")))
        val file = File(localDestination)
        file.writeBytes(bytes)

        res.add(Result.success(file.toPath()))
        return res
    }

    protected abstract fun checkFormat(bytes: ByteArray): Boolean
}
