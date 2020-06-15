package org.ionproject.integration.file.interfaces

import java.io.File

interface FileDigest {
    fun digest(f: File): ByteArray
}
