package org.ionproject.integration.infrastructure.file

import java.io.File

interface IFileDigest {
    fun digest(f: File): ByteArray
}
