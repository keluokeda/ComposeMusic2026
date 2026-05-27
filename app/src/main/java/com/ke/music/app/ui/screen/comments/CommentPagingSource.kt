package com.ke.music.app.ui.screen.comments

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ke.music.app.data.model.CommentVO
import com.ke.music.app.data.repository.CommentRepository

class CommentPagingSource(
    private val type: Int,
    private val id: Long,
    private val commentRepository: CommentRepository
) : PagingSource<Int, CommentVO>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommentVO> {
        try {
            val index = params.key ?: 1
            val response = commentRepository.comments(type, id, index)

            return LoadResult.Page(
                data = response.data,
                prevKey = null,
                nextKey = if (response.hasMore == true) index + 1 else null
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CommentVO>) = 1
}