package org.ionproject.integration.file.implementations

import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.channels.ReadableByteChannel
import java.nio.file.Path

const val TEST_PROFILE = "test"

interface ChannelProvider {
    fun getRemoteChannel(uri: URI): ReadableByteChannel

    fun getLocalChannel(localDestination: Path): FileChannel
}

@Service
@Primary
class ChannelProviderImpl : ChannelProvider {
    override fun getRemoteChannel(uri: URI): ReadableByteChannel =
        Channels.newChannel(uri.toURL().openStream())

    override fun getLocalChannel(localDestination: Path): FileChannel =
        FileOutputStream(localDestination.toFile()).channel
}

@Service
@Profile(TEST_PROFILE)
class MockChannelProvider : ChannelProvider {
    override fun getRemoteChannel(uri: URI): ReadableByteChannel =
        Channels.newChannel(File("LEIC_example.pdf").inputStream())

    override fun getLocalChannel(localDestination: Path): FileChannel =
        FileOutputStream(localDestination.toFile()).channel
}
