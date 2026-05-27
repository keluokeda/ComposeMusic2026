package com.ke.music.app.ui.screen.playlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ke.music.app.MusicApp
import com.ke.music.app.data.model.PlaylistDetailVO
import com.ke.music.app.data.repository.PlaylistRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel(assistedFactory = PlaylistDetailViewModel.Factory::class)
class PlaylistDetailViewModel @AssistedInject constructor(
    @Assisted  val id: Long,
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(id: Long): PlaylistDetailViewModel
    }

    sealed interface UiState {


        data object Loading : UiState

        data class Success(val response: PlaylistDetailVO) : UiState

        data object Error : UiState
    }

    var uiState by mutableStateOf<UiState>(UiState.Loading)
        private set

//    var detail by mutableStateOf<PlaylistDetailVO?>(null)
//        private set
//
//    var isLoading by mutableStateOf(false)
//        private set


    fun loadDetail() {
        viewModelScope.launch {
            uiState = UiState.Loading
            val response = playlistRepository.playlistDetail(id)
            if (response.success) {
                uiState = UiState.Success(response.data!!)
            } else {
                MusicApp.toast(response.message)
                uiState = UiState.Error
            }
//            isLoading = false
        }
    }

    init {
        loadDetail()
    }
}
