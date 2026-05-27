package com.ke.music.app.ui.screen.notification.notices

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ke.music.app.data.model.NotificationNoticeResponse
import com.ke.music.app.data.repository.MessageRepository

class NoticePagingSource(
    private val messageRepository: MessageRepository
) : PagingSource<Long, NotificationNoticeResponse>() {
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, NotificationNoticeResponse> {
        return try {
            val cursor = params.key
            val response = messageRepository.notices(cursor)
             LoadResult.Page(
                data = response.data,
                prevKey = null,
                nextKey = if (response.hasMore == true) response.cursor else null
            )
        } catch (e: Exception) {
             LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, NotificationNoticeResponse>): Long? {
        return null
    }
}