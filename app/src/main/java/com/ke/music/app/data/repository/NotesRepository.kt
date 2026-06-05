package com.ke.music.app.data.repository

import com.ke.music.app.data.api.MusicApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepository @Inject constructor(
    private val musicApiService: MusicApiService
) {
    suspend fun notes(lastTime: Long) = musicApiService.notes(lastTime)
}