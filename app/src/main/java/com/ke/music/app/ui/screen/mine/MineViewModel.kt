package com.ke.music.app.ui.screen.mine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ke.music.app.data.model.MineVO
import com.ke.music.app.data.repository.MineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor(
    private val mineRepository: MineRepository
) : ViewModel() {

    sealed interface UiState {
        data object Loading : UiState

        data object Error : UiState

        data class Success(val content: MineVO) : UiState
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
            val response = mineRepository.page()
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