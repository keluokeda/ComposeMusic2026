package com.ke.music.app.data.repository

import com.ke.music.app.data.api.MusicApiService
import com.ke.music.app.data.api.safeApiCall
import com.ke.music.app.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(
    private val apiService: MusicApiService
) {
    suspend fun playlistDetail(id: Long): BaseVO<PlaylistDetailVO> = safeApiCall { apiService.playlistDetail(id) }

    suspend fun deletePlaylist(id: Long): BaseVO<Unit> = safeApiCall { apiService.deletePlaylist(id) }

    suspend fun addToPlaylist(playlistId: Long, songIds: List<Long>): BaseVO<Unit> =
        safeApiCall { apiService.addToPlaylist(playlistId, songIds) }

    suspend fun deleteSongFromPlaylist(playlistId: Long, songId: Long): BaseVO<Unit> =
        safeApiCall { apiService.deleteSongFromPlaylist(playlistId, songId) }
}
