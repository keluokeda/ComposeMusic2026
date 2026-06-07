package com.ke.music.app.player

import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.ke.music.app.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlayer @Inject constructor(
    private val controllerFuture: ListenableFuture<MediaController>
) : IPlayer {

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentIndex = MutableStateFlow(0)
    override val currentIndex: StateFlow<Int> = _currentIndex

    private val _duration = MutableStateFlow(0L)
    override val duration: StateFlow<Long> = _duration

    private val _currentMetadata = MutableStateFlow<SongMetadata?>(null)
    override val currentMetadata: StateFlow<SongMetadata?> = _currentMetadata

    private val _repeatMode = MutableStateFlow(RepeatMode.LIST)
    override val repeatMode: StateFlow<RepeatMode> = _repeatMode

    private val _hasMediaItem = MutableStateFlow(false)
    override val hasMediaItem: StateFlow<Boolean> = _hasMediaItem

    override var songs: List<Song> = emptyList()
        private set

    private val iPlayerListeners = mutableListOf<IPlayerListener>()

    private val controller: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null

    init {
        controllerFuture.addListener({
            val c = controllerFuture.get()
            c.repeatMode = Player.REPEAT_MODE_ALL
            c.addListener(object : Player.Listener {

                override fun onIsPlayingChanged(playing: Boolean) {
                    _isPlaying.value = playing
                }

                override fun onEvents(player: Player, events: Player.Events) {
                    if (events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED) ||
                        events.contains(Player.EVENT_TIMELINE_CHANGED)
                    ) {
                        _duration.value = player.duration.coerceAtLeast(0L)
                        _hasMediaItem.value = player.currentMediaItem != null
                    }
                    if (events.contains(Player.EVENT_MEDIA_METADATA_CHANGED)) {
                        _currentMetadata.value = player.mediaMetadata.toSongMetadata()
                        _hasMediaItem.value = player.currentMediaItem != null
                    }
                    if (events.contains(Player.EVENT_REPEAT_MODE_CHANGED) ||
                        events.contains(Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED)
                    ) {
                        _repeatMode.value = player.toRepeatMode()
                    }
                    if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                        _currentIndex.value = player.currentMediaItemIndex
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    // 只处理 AUTO（歌曲自然播完自动跳下一首），其余 reason 均由直接调用方负责：
                    //   PLAYLIST_CHANGED → MusicViewModel.playSongs
                    //   SEEK            → skipToNext / skipToPrevious / playAtIndex
                    //   REPEAT          → 同一首歌重播，URL 已存在无需重拉（hasSongUrl 会拦截），
                    //                     但 setMediaItems 在某些情况下也会触发 REPEAT，必须过滤
                    if (reason != Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) return
                    val index = c.currentMediaItemIndex
                    _currentIndex.value = index
                    if (!hasSongUrl(index)) {
                        iPlayerListeners.forEach { it.onMediaItemTransition(index) }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    val index = c.currentMediaItemIndex
                    if (c.currentMediaItem != null && !hasSongUrl(index)) {
                        iPlayerListeners.forEach { it.onPlayerError(index) }
                    }
                }
            })
        }, MoreExecutors.directExecutor())
    }

    override fun getCurrentPosition(): Long = controller?.currentPosition ?: 0L

    override fun hasSongUrl(index: Int): Boolean {
        val c = controller ?: return false
        if (index >= c.mediaItemCount) return false
        return !isUriEmpty(c.getMediaItemAt(index))
    }

    override fun addListener(listener: IPlayerListener) {
        iPlayerListeners.add(listener)
    }

    override fun removeListener(listener: IPlayerListener) {
        iPlayerListeners.remove(listener)
    }

    override fun playSongs(songs: List<Song>, startPosition: Int) {
        this.songs = songs
        controllerFuture.addListener({
            val c = controllerFuture.get()
            val mediaItems = songs.map { song ->
                createMediaItem(
                    url = null,
                    name = song.name,
                    artist = song.artists.joinToString("/") { it.name },
                    imageUrl = song.album.imageUrl,
                    mediaId = song.id.toString()
                )
            }
            // 不调 prepare()：避免 ExoPlayer 在 REPEAT_MODE_ALL + 空 URI 下反复 AUTO 跳曲。
            // PLAYLIST_CHANGED / SEEK 均被 onMediaItemTransition 过滤，由 MusicViewModel 直接处理。
            c.setMediaItems(mediaItems, startPosition, 0L)
        }, MoreExecutors.directExecutor())
    }

    override fun playAtIndex(index: Int) {
        val c = controller ?: return
        // 先 pause 再 seek，防止 playWhenReady=true 对空 URI 触发 AUTO 跳曲循环。
        // 有 URL 时立即 prepare+play；没有 URL 时由 fetchAndPlay 拿到 URL 后再调此方法播放。
        c.pause()
        c.seekTo(index, 0L)
        _currentIndex.value = index
        if (hasSongUrl(index)) {
            c.prepare()
            c.play()
        }
    }

    override fun skipToNext() {
        val c = controller ?: return
        val nextIndex = c.nextMediaItemIndex
        if (nextIndex == C.INDEX_UNSET) return
        // 先 pause 再 seek：避免 playWhenReady=true 时 ExoPlayer 对空 URI 自动尝试播放，
        // 在 REPEAT_MODE_ALL 下会一直 AUTO 跳曲，直到找到有 URL 的歌（即当前曲）重播。
        c.pause()
        c.seekTo(nextIndex, 0L)
        _currentIndex.value = nextIndex
        if (hasSongUrl(nextIndex)) {
            c.prepare()
            c.play()
        } else {
            iPlayerListeners.forEach { it.onMediaItemTransition(nextIndex) }
        }
    }

    override fun skipToPrevious() {
        val c = controller ?: return
        val prevIndex = c.previousMediaItemIndex
        if (prevIndex == C.INDEX_UNSET) return
        c.pause()
        c.seekTo(prevIndex, 0L)
        _currentIndex.value = prevIndex
        if (hasSongUrl(prevIndex)) {
            c.prepare()
            c.play()
        } else {
            iPlayerListeners.forEach { it.onMediaItemTransition(prevIndex) }
        }
    }

    override fun pause() {
        controller?.pause()
    }

    override fun resume() {
        controller?.play()
    }

    override fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
    }

    override fun toggleRepeatMode() {
        val c = controller ?: return
        when {
            !c.shuffleModeEnabled && c.repeatMode == Player.REPEAT_MODE_ALL -> {
                c.repeatMode = Player.REPEAT_MODE_ONE
            }
            !c.shuffleModeEnabled && c.repeatMode == Player.REPEAT_MODE_ONE -> {
                c.repeatMode = Player.REPEAT_MODE_ALL
                c.shuffleModeEnabled = true
            }
            else -> {
                c.repeatMode = Player.REPEAT_MODE_ALL
                c.shuffleModeEnabled = false
            }
        }
    }

    override fun startPlayback() {
        val c = controller ?: return
        c.prepare()
        c.play()
    }

    override fun updateSongUrl(index: Int, url: String, onUpdated: () -> Unit) {
        val song = songs.getOrNull(index) ?: return
        controllerFuture.addListener({
            val c = controllerFuture.get()
            // 直接用本地 songs 构建 MediaItem，避免依赖 c.mediaItemCount / c.getMediaItemAt(index)。
            // MediaController 本地状态在 setMediaItems 后可能尚未同步（异步 IPC），
            // 但 session 侧的 ExoPlayer 已按顺序处理了 setMediaItems，replaceMediaItem 会正确执行。
            val newItem = createMediaItem(
                url = url,
                name = song.name,
                artist = song.artists.joinToString("/") { it.name },
                imageUrl = song.album.imageUrl,
                mediaId = song.id.toString()
            )
            c.replaceMediaItem(index, newItem)
            onUpdated()
        }, MoreExecutors.directExecutor())
    }

    private fun isUriEmpty(item: MediaItem): Boolean {
        val uri = item.localConfiguration?.uri ?: return true
        return uri.toString().isEmpty()
    }

    private fun createMediaItem(
        url: String?,
        name: String,
        artist: String,
        imageUrl: String?,
        mediaId: String? = null
    ): MediaItem = MediaItem.Builder()
        .apply { if (!url.isNullOrEmpty()) setUri(url) }
        .setMediaId(mediaId ?: url.orEmpty())
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(name)
                .setArtist(artist)
                .setArtworkUri(imageUrl?.toUri())
                .build()
        )
        .build()

    private fun MediaMetadata.toSongMetadata() = SongMetadata(
        title = title?.toString(),
        artist = artist?.toString(),
        artworkUrl = artworkUri?.toString()
    )

    private fun Player.toRepeatMode(): RepeatMode = when {
        shuffleModeEnabled -> RepeatMode.SHUFFLE
        repeatMode == Player.REPEAT_MODE_ONE -> RepeatMode.ONE
        else -> RepeatMode.LIST
    }
}
