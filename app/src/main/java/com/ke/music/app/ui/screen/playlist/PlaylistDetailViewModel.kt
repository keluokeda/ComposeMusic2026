package com.ke.music.app.ui.screen.playlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ke.music.app.data.model.PlaylistDetailVO
import com.ke.music.app.data.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    var detail by mutableStateOf<PlaylistDetailVO?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set



    fun loadDetail(id: Long) {
        viewModelScope.launch {
            isLoading = true
            val response = playlistRepository.playlistDetail(id)
            if (response.success) {
                detail = response.data
            }
            isLoading = false
        }
    }
}
