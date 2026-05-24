package com.ke.music.app.ui.screen.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ke.music.app.data.model.UserDetailVO
import com.ke.music.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    var detail by mutableStateOf<UserDetailVO?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun loadDetail(id: Long) {
        viewModelScope.launch {
            isLoading = true
            val response = userRepository.userDetail(id)
            if (response.success) {
                detail = response.data
            }
            isLoading = false
        }
    }
}
