package org.ionproject.integration.infrastructure.file

import java.io.File
import java.security.MessageDigest

private const val HASH_ALGORITHM = "SHA-256"

class FileDigestImpl : IFileDigest {
    override fun digest(f: File): ByteArray {
        val byteArray = f.readBytes()
        val md = MessageDigest.getInstance(HASH_ALGORITHM)
        return md.digest(byteArray)
    }
}
