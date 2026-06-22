package com.ke.music.app.player

import com.ke.music.app.data.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

data class SongMetadata(
    val id: Long,
    val title: String?,
    val artist: String?,
    val artworkUrl: String?
)

enum class RepeatMode { LIST, ONE, SHUFFLE }

interface IPlayerController{
    /**
     * 播放歌曲列表
     * @return 成功播放返回true，如果当前正在播放返回false
     */
    fun playSongs(songs: List<Song>, startPosition: Int): Boolean
    fun playAtIndex(index: Int)
    fun skipToNext()
    fun skipToPrevious()
    fun pause()
    fun resume()
    fun seekTo(positionMs: Long)
    fun toggleRepeatMode()

    fun insertIntoCurrentPlaylist(song: Song,playNow: Boolean): Boolean
}


interface IPlayer: IPlayerController {

    val songs: StateFlow<List<Song>>

    val currentLrc: StateFlow<String?>
    val isPlaying: StateFlow<Boolean>
    val currentIndex: StateFlow<Int>
    val duration: StateFlow<Long>
    val currentMetadata: StateFlow<SongMetadata?>
    val repeatMode: StateFlow<RepeatMode>
    val hasMediaItem: StateFlow<Boolean>

    val currentSongLiked: StateFlow<Boolean>


    /** 当前播放进度（毫秒），UI 轮询 */
    fun getCurrentPosition(): Long

    /** index 对应的条目是否已有可播放 URL */
    fun hasSongUrl(index: Int): Boolean





    fun bindScope(scope: CoroutineScope)


    fun toggleLike()
}
