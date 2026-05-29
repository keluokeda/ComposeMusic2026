package com.ke.music.app.ui.screen.recommend

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ke.music.app.data.model.RecommendVO
import com.ke.music.app.data.repository.RecommendRepository
import com.ke.music.app.ui.screen.my_playlists.MyPlaylistsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendViewModel @Inject constructor(
    private val recommendRepository: RecommendRepository
) : ViewModel() {

    sealed interface UiState {
        data object Loading : UiState

        data object Error : UiState

        data class Success(val content: RecommendVO) : UiState
    }

    var uiState by mutableStateOf<UiState>(UiState.Loading)
        private set

    var isRefreshing by mutableStateOf(false)
        private set


    fun refresh() {
        viewModelScope.launch {
            if (uiState == UiState.Error) {
                uiState = UiState.Loading
            }
            isRefreshing = true
            val response = recommendRepository.recommend()
            if (response.success) {
                uiState = UiState.Success(response.data!!)
            } else {
                uiState = UiState.Error
            }
            isRefreshing = false
        }
    }

    init {
        refresh()
    }
}