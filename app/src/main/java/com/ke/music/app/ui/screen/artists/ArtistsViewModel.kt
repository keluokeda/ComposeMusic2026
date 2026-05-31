package com.ke.music.app.ui.screen.artists

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ke.music.app.data.repository.ArtistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArtistsViewModel @Inject constructor(
    private val artistRepository: ArtistRepository
) : ViewModel() {

    var artistType by mutableStateOf(ArtistType.All)
    var artistArea by mutableStateOf(ArtistArea.All)

    val artists = Pager(config = PagingConfig(enablePlaceholders = false, pageSize = 20, initialLoadSize = 20)){
        ArtistsPagingSource(artistRepository,artistType.type,artistArea.area)
    }.flow.cachedIn(viewModelScope)

    //-1全部 1男歌手 2女歌手 3乐队
    enum class ArtistType(val displayName: String, val type: Int) {
        All("全部", -1),
        Man("男歌手", 1),
        Women("女歌手", 2),
        Band("乐队", 3)
    }

    //-1全部  7华语 96欧美 8日本 16韩国 0其他
    enum class ArtistArea(val displayName: String, val area: Int) {
        All("全部", -1),
        Chinese("华语", 7),
        EuropeAndAmerica("欧美", 96),
        Japan("日本", 8),
        Korean("韩国", 16),
        Other("其他", 0)
    }
}