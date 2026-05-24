package com.ke.music.app.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ke.music.app.data.model.Playlist
import com.ke.music.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    var playlists by mutableStateOf<List<Playlist>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun loadPlaylists() {
        viewModelScope.launch {
            isLoading = true
            val response = userRepository.currentUserPlaylists()
            if (response.success) {
                playlists = response.data ?: emptyList()
            }
            isLoading = false
        }
    }
}
