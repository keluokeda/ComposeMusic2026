package com.ke.music.app.ui.screen.artist_fans

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ke.music.app.data.model.ArtistFansResponse
import com.ke.music.app.data.repository.ArtistRepository

class ArtistFansPagingSource (
    private val artistRepository: ArtistRepository,
    private val artistId: Long
) : PagingSource<Int, ArtistFansResponse.UserProfile>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArtistFansResponse.UserProfile> {
        try {
            val index = params.key ?: 1
            val response = artistRepository.artistFans(artistId, index)
            val list = response.data!!.map { it.userProfile }
            return LoadResult.Page(
                data = list,
                prevKey = null,
                nextKey = if (list.isEmpty()) null else index + 1

            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ArtistFansResponse.UserProfile>): Int = 1
}