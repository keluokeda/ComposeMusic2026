package com.ke.music.app.data.repository

import com.ke.music.app.data.api.MusicApiService
import com.ke.music.app.data.api.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepository @Inject constructor(private val musicApiService: MusicApiService) {

    suspend fun recentSongs() = safeApiCall {
        musicApiService.recentSongs()
    }

    suspend fun isLiked(id: Long) = safeApiCall { musicApiService.isLikeSong(id) }
    suspend fun detail(id: Long) = safeApiCall { musicApiService.songDetail(id) }

    suspend fun lrc(id: Long) = safeApiCall { musicApiService.songLrc(id) }

    suspend fun likeSong(id: Long, like: Boolean) = safeApiCall { musicApiService.likeSong(id, like) }
}