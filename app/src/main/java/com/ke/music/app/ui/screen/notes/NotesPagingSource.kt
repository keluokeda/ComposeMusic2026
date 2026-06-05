package com.ke.music.app.ui.screen.notes

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ke.music.app.data.model.EventVO
import com.ke.music.app.data.repository.NotesRepository

class NotesPagingSource(
    private val notesRepository: NotesRepository
) : PagingSource<Long, EventVO>() {
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, EventVO> {
        try {
            val response = notesRepository.notes(params.key ?: -1)
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

    override fun getRefreshKey(state: PagingState<Long, EventVO>): Long = -1
}