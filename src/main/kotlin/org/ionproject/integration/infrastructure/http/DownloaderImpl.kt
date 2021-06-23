package org.ionproject.integration.infrastructure.http

import org.ionproject.integration.utils.Try
import org.springframework.stereotype.Service
import java.net.URI
import java.nio.file.Path

@Service
class DownloaderImpl(val channelProvider: ChannelProvider) : IFileDownloader {
    override fun download(uri: URI, localDestination: Path): Try<Path> {
        channelProvider.getRemoteChannel(uri).use { remoteChannel ->
            channelProvider.getLocalChannel(localDestination).use { fileChannel ->
                return Try.of {
                    fileChannel.transferFrom(remoteChannel, 0, Long.MAX_VALUE)
                    localDestination
                }
            }
        }
    }
}
