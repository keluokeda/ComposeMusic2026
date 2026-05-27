package com.ke.music.app.data.repository

import com.ke.music.app.data.api.MusicApiService
import com.ke.music.app.data.api.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(
    private val apiService: MusicApiService
) {

    suspend fun comments(type: Int, id: Long, index: Int, pageSize: Int = 20) =
        apiService.getComments(type, id, index, pageSize)

}