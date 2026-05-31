package com.ke.music.app.ui.screen.top_playlists

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ke.music.app.data.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopPlaylistsViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {
    var category by mutableStateOf<String?>(null)
        private set

    var categoryList by mutableStateOf<List<String>>(emptyList())
        private set

    val playlists = Pager(
        config = PagingConfig(
            enablePlaceholders = false,
            initialLoadSize = 20,
            pageSize = 20
        )
    ) {
        TopPlaylistsPagingSource(playlistRepository, category)
    }.flow.cachedIn(viewModelScope)

    fun updateCategory(category: String){
        if(category == this.category){
            this.category = null
        }else{
            this.category = category
        }
    }

    init {
        viewModelScope.launch {
            categoryList = playlistRepository.topPlaylistTags().data?:emptyList()
        }
    }
}