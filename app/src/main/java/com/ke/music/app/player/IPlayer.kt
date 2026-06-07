package com.ke.music.app.player

import com.ke.music.app.data.model.Song
import kotlinx.coroutines.flow.StateFlow

data class SongMetadata(
    val title: String?,
    val artist: String?,
    val artworkUrl: String?
)

enum class RepeatMode { LIST, ONE, SHUFFLE }

//interface IPlayerListener {
//    /** 切歌（PLAYLIST_CHANGED 由调用方直接处理，此处不触发） */
//    fun onMediaItemTransition(index: Int) {}
//    /** 播放出错且当前条目无 URL，需 ViewModel 去拉地址 */
//    fun onPlayerError(index: Int) {}
//}

interface IPlayer {

    val songs: List<Song>

    // 可观察状态，UI 直接 collectAsState()
    val isPlaying: StateFlow<Boolean>
    val currentIndex: StateFlow<Int>
    val duration: StateFlow<Long>
    val currentMetadata: StateFlow<SongMetadata?>
    val repeatMode: StateFlow<RepeatMode>
    val hasMediaItem: StateFlow<Boolean>

    /** 当前播放进度（毫秒），UI 轮询 */
    fun getCurrentPosition(): Long

    /** index 对应的条目是否已有可播放 URL */
    fun hasSongUrl(index: Int): Boolean

//    fun addListener(listener: IPlayerListener)
//    fun removeListener(listener: IPlayerListener)

    fun playSongs(songs: List<Song>, startPosition: Int)
    fun playAtIndex(index: Int)
    fun skipToNext()
    fun skipToPrevious()
    fun pause()
    fun resume()
    fun seekTo(positionMs: Long)
    fun toggleRepeatMode()

    /** 拿到 URL 后回填，URL 写入完成后调用 onUpdated */
    fun updateSongUrl(index: Int, url: String, onUpdated: () -> Unit = {})

    /** 从当前位置开始播放（prepare + play），供 fetchAndPlay 拿到 URL 后调用 */
    fun startPlayback()
}
