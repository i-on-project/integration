package org.ionproject.integration.file.implementations

import java.io.File
import java.security.MessageDigest
import org.ionproject.integration.file.interfaces.FileDigest

private const val HASH_ALGORITHM = "SHA-256"

class FileDigestImpl : FileDigest {
    override fun digest(f: File): ByteArray {
        val byteArray = f.readBytes()
        val md = MessageDigest.getInstance(HASH_ALGORITHM)
        return md.digest(byteArray)
    }
}
