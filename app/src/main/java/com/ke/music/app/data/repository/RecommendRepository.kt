package com.ke.music.app.data.repository

import com.ke.music.app.data.api.MusicApiService
import com.ke.music.app.data.api.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendRepository @Inject constructor(
    private val musicApiService: MusicApiService
) {

    suspend fun recommend() = safeApiCall {
//        musicApiService.recentSongs()
        musicApiService.recommend()
    }
}