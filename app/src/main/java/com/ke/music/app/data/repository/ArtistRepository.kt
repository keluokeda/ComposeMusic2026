package com.ke.music.app.data.repository

import com.ke.music.app.data.api.MusicApiService
import com.ke.music.app.data.api.safeApiCall
import com.ke.music.app.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistRepository @Inject constructor(
    private val apiService: MusicApiService
) {
    suspend fun getArtistDetail(id: Long): BaseVO<ArtistDetailVO> = safeApiCall { apiService.getArtistDetail(id) }
}
