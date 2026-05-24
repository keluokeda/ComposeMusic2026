package com.ke.music.app.data.repository

import com.ke.music.app.data.api.MusicApiService
import com.ke.music.app.data.api.safeApiCall
import com.ke.music.app.data.model.*
import com.ke.music.app.data.store.AppDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: MusicApiService,
    private val appDataStore: AppDataStore
) {
    suspend fun createLoginKey(): BaseVO<CreateLoginKeyVO> =
        safeApiCall { apiService.createLoginKey() }

    suspend fun login(key: String): BaseVO<LoginVO> = safeApiCall {
        apiService.login(key).apply {
            if (success) {
                appDataStore.token = data?.token
            }
        }
    }

    suspend fun loginStatus(): BaseVO<Long> = safeApiCall { apiService.loginStatus() }

    suspend fun userDetail(id: Long): BaseVO<UserDetailVO> =
        safeApiCall { apiService.userDetail(id) }

    suspend fun currentUserPlaylists(): BaseVO<List<Playlist>> =
        safeApiCall { apiService.currentUserPlaylists() }
}
