package com.ke.music.app.ui.screen.my_playlists

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ke.music.app.MusicApp
import com.ke.music.app.data.model.Playlist
import com.ke.music.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPlaylistsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    sealed interface UiState {
        data object Loading : UiState
        data class Success(val playlists: List<Playlist>) : UiState

        data object Error : UiState

    }

    var uiState by mutableStateOf<UiState>(UiState.Loading)
        private set


    fun loadPlaylists() {
        viewModelScope.launch {
            uiState = UiState.Loading
            val response = userRepository.currentUserPlaylists()
            if (response.success) {
                uiState = UiState.Success(response.data!!)
            } else {
                MusicApp.toast(response.message)
                uiState = UiState.Error
            }

        }
    }

    init {
        loadPlaylists()
    }
}
