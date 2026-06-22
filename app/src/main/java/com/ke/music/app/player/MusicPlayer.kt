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
import com.ke.music.app.MusicApp
import com.ke.music.app.data.model.Song
import com.ke.music.app.data.repository.SongRepository
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlayer @Inject constructor(
    private val controllerFuture: ListenableFuture<MediaController>,
    private val songRepository: SongRepository
) : IPlayer {

//    private val lrcMap: MutableMap<Long, String?> = emptyMap()

    private val _currentLrc = MutableStateFlow<String?>(null)

    override val currentLrc: StateFlow<String?> = _currentLrc.asStateFlow()

    private val _currentSongLiked = MutableStateFlow(false)

    override val currentSongLiked: StateFlow<Boolean> = _currentSongLiked.asStateFlow()
    private lateinit var scope: CoroutineScope

    override fun bindScope(scope: CoroutineScope) {
        this.scope = scope
    }

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    override val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    override val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _currentMetadata = MutableStateFlow<SongMetadata?>(null)
    override val currentMetadata: StateFlow<SongMetadata?> = _currentMetadata.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.LIST)
    override val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    private val _hasMediaItem = MutableStateFlow(false)
    override val hasMediaItem: StateFlow<Boolean> = _hasMediaItem.asStateFlow()

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    override var songs: StateFlow<List<Song>> = _songs.asStateFlow()


    private val controller: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null

    init {
        controllerFuture.addListener({
            val c = controllerFuture.get()
            c.repeatMode = Player.REPEAT_MODE_ALL
            c.playWhenReady = true
            c.addListener(object : Player.Listener {

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    Logger.d("onMediaMetadataChanged ${c.currentMediaItemIndex}")
//                    super.onMediaMetadataChanged(mediaMetadata)

                    val song = songs.value.getOrNull(c.currentMediaItemIndex) ?: return

                    _currentMetadata.value = mediaMetadata.toSongMetadata(song.id)

                    scope.launch {
                        loadLrc(song.id)
                        isLikeSong(song.id)
                    }


                }

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
//                    if (events.contains(Player.EVENT_MEDIA_METADATA_CHANGED)) {
//                        _currentMetadata.value = player.mediaMetadata.toSongMetadata()
//                        _hasMediaItem.value = player.currentMediaItem != null
//                    }
                    if (events.contains(Player.EVENT_REPEAT_MODE_CHANGED) ||
                        events.contains(Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED)
                    ) {
                        _repeatMode.value = player.toRepeatMode()
                    }
                    if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                        _currentIndex.value = player.currentMediaItemIndex
                    }
                }

                //MEDIA_ITEM_TRANSITION_REASON_REPEAT 0
                //MEDIA_ITEM_TRANSITION_REASON_AUTO 1
                //MEDIA_ITEM_TRANSITION_REASON_SEEK 2
                //MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED 3
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    Logger.d("mediaItem = $mediaItem , reason = $reason , currentMediaItemIndex = ${c.currentMediaItemIndex}")
                    //手动下一首 先2后3
                    //点击歌单播放 303
                }

                override fun onPlayerError(error: PlaybackException) {

                    if (error.errorCode == PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND) {
                        //资源有问题
                        val index = c.currentMediaItemIndex
                        fetchAndPlay(index)
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

    private suspend fun isLikeSong(id: Long) {
        _currentSongLiked.value = false
        _currentSongLiked.value = songRepository.isLiked(id).data ?: false
    }

    private suspend fun loadLrc(id: Long) {
        _currentLrc.value = null
        val response = songRepository.lrc(id)
        if (response.success) {
            _currentLrc.value = response.data
        }
    }

    override fun toggleLike() {
        val index = currentIndex.value
        val song = songs.value.getOrNull(index) ?: return
        val targetLike = !currentSongLiked.value

        scope.launch {
            val response = songRepository.likeSong(song.id, targetLike)
            if (response.success) {
                _currentSongLiked.value = targetLike
            } else {
                MusicApp.toast(response.message.ifEmpty { "操作失败" })
            }
        }
    }

    private fun fetchAndPlay(index: Int) {
        val song = songs.value.getOrNull(index) ?: return
        if (hasSongUrl(index)) return


        scope.launch {
            try {
//                loadLrc(song.id)
                val response = songRepository.detail(song.id)
                if (response.success && response.data?.url != null) {

                    updateSongUrl(index, response.data.url)
                    startPlayback()
                } else {
                    MusicApp.toast(response.message.ifEmpty { "获取歌曲地址失败" })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun playSongs(songs: List<Song>, startPosition: Int): Boolean {

//        controllerFuture.addListener({


        val c = controller ?: return true
        if (songs == _songs.value && startPosition == c.currentMediaItemIndex) {
            return false
        } else if (songs == _songs.value) {
            playAtIndex(startPosition)
            return true
        }

        _songs.value = songs


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
        c.prepare()
        c.play()
        return true
//        }, MoreExecutors.directExecutor())
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
//        c.pause()
        c.seekTo(nextIndex, 0L)
        _currentIndex.value = nextIndex
//        if (hasSongUrl(nextIndex)) {
//            c.prepare()
//            c.play()
//        } else {
//            iPlayerListeners.forEach { it.onMediaItemTransition(nextIndex) }
//        }
    }

    override fun skipToPrevious() {
        val c = controller ?: return
        val prevIndex = c.previousMediaItemIndex
        if (prevIndex == C.INDEX_UNSET) return
//        c.pause()
        c.seekTo(prevIndex, 0L)
        _currentIndex.value = prevIndex
//        if (hasSongUrl(prevIndex)) {
//            c.prepare()
//            c.play()
//        } else {
//            iPlayerListeners.forEach { it.onMediaItemTransition(prevIndex) }
//        }
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

    override fun insertIntoCurrentPlaylist(song: Song, playNow: Boolean): Boolean {
        val index = _songs.value.indexOf(song)
        if (index >= 0) {
            //包含当前歌曲
            if (index == currentIndex.value) {
                return false
            } else {
                playAtIndex(index)
                return true
            }
        }

        if(songs.value.isEmpty()){
            playSongs(listOf(song),0)
            return true
        }

        if(playNow){
            playSongs(songs.value.toMutableList().apply {
                add(currentIndex.value+1,song)
            },currentIndex.value+1)
        }
//        _songs.value = _songs.value.toMutableList()
//            .apply {
//                add(currentIndex.value + 1, song)
//            }
//
//        if (playNow) {
//            playAtIndex(currentIndex.value + 1)
//        }

        return true
    }

    private fun startPlayback() {
        val c = controller ?: return
        c.prepare()
        c.play()
    }

    private fun updateSongUrl(index: Int, url: String) {
        val song = songs.value.getOrNull(index) ?: return
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
        .setUri(url ?: "")
        .setMediaId(mediaId ?: url.orEmpty())
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(name)
                .setArtist(artist)
                .setArtworkUri(imageUrl?.toUri())
                .build()
        )
        .build()

    private fun MediaMetadata.toSongMetadata(id: Long) = SongMetadata(
        title = title?.toString(),
        artist = artist?.toString(),
        artworkUrl = artworkUri?.toString(),
        id = id
    )

    private fun Player.toRepeatMode(): RepeatMode = when {
        shuffleModeEnabled -> RepeatMode.SHUFFLE
        repeatMode == Player.REPEAT_MODE_ONE -> RepeatMode.ONE
        else -> RepeatMode.LIST
    }
}
