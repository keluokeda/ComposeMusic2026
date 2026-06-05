package com.ke.music.app.player

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class MusicPlayer @Inject constructor(
    private val controllerFuture: ListenableFuture<MediaController>
) {

    val player: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null

    private val controller: MediaController?
        get() = player

    fun play(url: String, name: String, artist: String, imageUrl: String?, append: Boolean = false) {
        controllerFuture.addListener({
            val controller = controllerFuture.get()
            val mediaItem = createMediaItem(url, name, artist, imageUrl)
            
            if (append) {
                controller.addMediaItem(mediaItem)
            } else {
                controller.setMediaItem(mediaItem)
                controller.prepare()
                controller.play()
            }
        }, MoreExecutors.directExecutor())
    }

    fun playNext(url: String, name: String, artist: String, imageUrl: String?) {
        controllerFuture.addListener({
            val controller = controllerFuture.get()
            val mediaItem = createMediaItem(url, name, artist, imageUrl)
            val nextIndex = if (controller.mediaItemCount > 0) controller.currentMediaItemIndex + 1 else 0
            controller.addMediaItem(nextIndex, mediaItem)
        }, MoreExecutors.directExecutor())
    }

    fun playList(urls: List<String>, names: List<String>, artists: List<String>, imageUrls: List<String?>, startIndex: Int = 0, append: Boolean = false) {
        controllerFuture.addListener({
            val controller = controllerFuture.get()
            val mediaItems = urls.mapIndexed { index, url ->
                createMediaItem(url, names[index], artists[index], imageUrls[index])
            }
            if (append) {
                controller.addMediaItems(mediaItems)
            } else {
                controller.setMediaItems(mediaItems, startIndex, 0L)
                controller.prepare()
                controller.play()
            }
        }, MoreExecutors.directExecutor())
    }

    private fun createMediaItem(url: String, name: String, artist: String, imageUrl: String?): MediaItem {
        return MediaItem.Builder()
            .setUri(url)
            .setMediaId(url)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(name)
                    .setArtist(artist)
                    .setArtworkUri(imageUrl?.toUri())
                    .build()
            )
            .build()
    }

    fun skipToNext() {
        controller?.seekToNext()
    }

    fun skipToPrevious() {
        controller?.seekToPrevious()
    }

    fun stop() {
        controller?.stop()
    }

    fun pause() {
        controller?.pause()
    }

    fun resume() {
        controller?.play()
    }
}
