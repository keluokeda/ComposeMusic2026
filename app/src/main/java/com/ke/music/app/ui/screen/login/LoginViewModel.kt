package com.ke.music.app.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ke.music.app.MusicApp
import com.ke.music.app.data.model.CreateLoginKeyVO
import com.ke.music.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private var key: String = ""

    sealed interface UiState {
        data object Loading : UiState

        data class Success(
            val url: String,
            val loading: Boolean = false,
            val success: Boolean = false
        ) :
            UiState

        data object Error : UiState
    }


    var uiState by mutableStateOf<UiState>(UiState.Loading)
        private set




    fun createKey() {
        viewModelScope.launch {
            uiState = UiState.Loading
            val response = userRepository.createLoginKey()
            uiState = if (response.success) {
                key = response.data!!.key
                UiState.Success(url = response.data.url)
            } else {
                UiState.Error
            }
        }
    }

    fun checkLogin(url: String){
        viewModelScope.launch {
            uiState = UiState.Success(url,loading = true)
            val response = userRepository.login(key)
            if(response.success){
                uiState = UiState.Success(url,loading = false,success = true)
            }else{
                MusicApp.toast(response.message)
                uiState = UiState.Success(url,loading = false)
            }
        }
    }


    init {
        createKey()
    }

}
