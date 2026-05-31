package com.ke.music.app.ui.screen.user_status

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ke.music.app.MusicApp
import com.ke.music.app.data.model.MineVO
import com.ke.music.app.data.model.SameStatusUserResponse
import com.ke.music.app.data.model.UserStatusVO
import com.ke.music.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserStatusViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    var isRefreshing by mutableStateOf(false)
        private set

    var loading by mutableStateOf(false)
        private set

    var uiState by mutableStateOf<UiState>(UiState.Loading)
        private set

    sealed interface UiState {
        data object Loading : UiState
        data object Error : UiState
        data class Success(
            val content: UserStatusVO,
            val sameUser: SameStatusUserResponse? = null
        ) :
            UiState
    }

    fun refresh() {

        viewModelScope.launch {
            if (uiState == UiState.Error) {
                uiState = UiState.Loading
            }
            isRefreshing = true
            val response = userRepository.userStatus()
            isRefreshing = false
            if (response.success) {
                uiState = UiState.Success(response.data!!)
            } else {
                uiState = UiState.Error
            }
        }
    }

    init {
        refresh()
    }

    fun commit(status: MineVO.UserSupportStatus) {
        viewModelScope.launch {
            loading = true

            val response = userRepository.updateUserStatus(status)
            loading = false
            if (response.success) {
                val old = (uiState as UiState.Success)
                uiState = old.copy(
                    sameUser = response.data, content = old.content.copy(
                        current = status
                    )
                )
            } else {
                MusicApp.toast(response.message)
            }
        }
    }
}