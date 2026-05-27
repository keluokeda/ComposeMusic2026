package com.ke.music.app.data.repository

import com.ke.music.app.data.api.MusicApiService
import com.ke.music.app.data.api.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val musicApiService: MusicApiService
) {
    suspend fun messageList() = safeApiCall {
        musicApiService.getAllMessages()
    }

    suspend fun notificationComments(before: Long? = null) = musicApiService.notificationComments(before)

    suspend fun notices(cursor: Long?) = musicApiService.notices(cursor)
}