package org.ionproject.integration.file.interfaces

import org.ionproject.integration.model.internal.generic.JobType

interface IBytesFormatChecker {
    fun checkFormat(bytes: ByteArray, jobType: JobType?)
}
