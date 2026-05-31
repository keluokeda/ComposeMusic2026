package com.ke.music.app.ui.screen.top_playlists

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ke.music.app.data.model.Playlist
import com.ke.music.app.data.repository.PlaylistRepository

class TopPlaylistsPagingSource(
    private val playlistRepository: PlaylistRepository,
    private val category: String? = null
) : PagingSource<Long, Playlist>() {
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Playlist> {
        try {
            val before = params.key ?: 0
            val response = playlistRepository.topPlaylists(category, before, 20)

            return LoadResult.Page(
                data = response.data,
                prevKey = null,
                nextKey = if (response.hasMore == true) response.cursor else null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, Playlist>): Long? {
        return 0
    }
}