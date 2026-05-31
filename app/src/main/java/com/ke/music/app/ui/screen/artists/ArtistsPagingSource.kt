package com.ke.music.app.ui.screen.artists

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ke.music.app.data.model.Artist
import com.ke.music.app.data.repository.ArtistRepository

class ArtistsPagingSource(
    private val artistRepository: ArtistRepository,
    private val type: Int,
    private val area: Int
) : PagingSource<Int, Artist>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Artist> {
        try {
            val index = params.key ?: 1
            val response = artistRepository.artists(type, area, index)
            return LoadResult.Page(
                data = response.data,
                prevKey = null,
                nextKey = if (response.hasMore == true) index + 1 else null
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Artist>) = 1
}