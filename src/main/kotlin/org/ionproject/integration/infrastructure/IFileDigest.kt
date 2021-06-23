package org.ionproject.integration.infrastructure

import java.io.File

interface IFileDigest {
    fun digest(f: File): ByteArray
}
