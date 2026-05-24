package com.ke.music.app.ui.screen.splash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ke.music.app.MusicApp
import com.ke.music.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    var uiState by mutableStateOf(UiState())
        private set

    data class UiState(
        val error: Boolean = false,
        val userId: Long? = null
    )

    fun checkLogin(){
        viewModelScope.launch {
            uiState = UiState(error = false)
            val response = userRepository.loginStatus()
            if(response.success){
                uiState = UiState(error = false, userId = response.data)
            }else if(response.code == 401){
                uiState = UiState(userId = 0)
            }else{
                MusicApp.toast(response.message)
                uiState = UiState(error = true)
            }
        }
    }

    init {
        checkLogin()
    }
}