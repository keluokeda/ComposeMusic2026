package com.ke.music.app.ui.screen.notification.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.ke.music.app.data.model.NotificationComment
import com.ke.music.app.data.repository.MessageRepository
import com.ke.music.app.ui.screen.comments.CommentPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationCommentViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    val list = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false, initialLoadSize = 20),
        pagingSourceFactory = {
           NotificationCommentPagingSource(messageRepository)
        }
    ).flow.cachedIn(viewModelScope)
}

class NotificationCommentPagingSource(
    private val messageRepository: MessageRepository
) : PagingSource<Long, NotificationComment>() {
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, NotificationComment> {
        return try {
            val before = params.key
            val response = messageRepository.notificationComments(before)

             LoadResult.Page(
                data = response.data,
                prevKey = null,
                nextKey = if (response.hasMore == true) response.cursor else null
            )
        } catch (e: Exception) {
             LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, NotificationComment>): Long? {
        return null
    }

}
