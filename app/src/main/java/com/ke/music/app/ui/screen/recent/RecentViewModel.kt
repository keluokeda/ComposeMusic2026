package com.ke.music.app.ui.screen.recent

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ke.music.app.data.model.Song
import com.ke.music.app.data.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentViewModel @Inject constructor(
    private val songRepository: SongRepository
) : ViewModel() {
    var recentSongs by mutableStateOf<List<Song>>(emptyList())
        private set

    init {
        viewModelScope.launch {
            recentSongs = songRepository.recentSongs().data ?: emptyList()
        }
    }
}