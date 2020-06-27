package org.ionproject.integration.file.interfaces

import java.io.File

interface IFileDigest {
    fun digest(f: File): ByteArray
}
