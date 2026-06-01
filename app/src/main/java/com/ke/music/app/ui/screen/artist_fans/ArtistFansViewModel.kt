package com.ke.music.app.ui.screen.artist_fans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ke.music.app.data.repository.ArtistRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = ArtistFansViewModel.Factory::class)
class ArtistFansViewModel @AssistedInject constructor(
    private val artistRepository: ArtistRepository,
    @Assisted private val id: Long
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(id: Long): ArtistFansViewModel
    }

    val items = Pager(
        config = PagingConfig(enablePlaceholders = false, initialLoadSize = 20, pageSize = 20)
    ) {
        ArtistFansPagingSource(artistRepository, id)
    }.flow.cachedIn(viewModelScope)
}