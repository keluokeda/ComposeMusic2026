package com.ke.music.app.ui.screen.artist_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ke.music.app.data.model.ArtistDetailVO
import com.ke.music.app.data.repository.ArtistRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ArtistDetailViewModel.Factory::class)
class ArtistDetailViewModel @AssistedInject constructor(
    private val artistRepository: ArtistRepository,
    @Assisted private val id: Long
) : ViewModel() {

    var selectedTabIndex by mutableIntStateOf(1)

    sealed interface UiState {
        data object Loading : UiState

        data object Error : UiState

        data class Success(val content: ArtistDetailVO) : UiState
    }

    var uiState by mutableStateOf<UiState>(UiState.Loading)
        private set

    @AssistedFactory
    interface Factory {
        fun create(id: Long): ArtistDetailViewModel
    }

    fun refresh(){
        viewModelScope.launch {
            uiState = UiState.Loading
            val response = artistRepository.getArtistDetail(id)
            uiState = if(response.success){
                UiState.Success(response.data!!)
            }else{
                UiState.Error
            }
        }
    }

    init {
        refresh()
    }
}