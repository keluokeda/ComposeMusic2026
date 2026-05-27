package com.ke.music.app.ui.screen.notification.notices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ke.music.app.data.repository.MessageRepository
import com.ke.music.app.ui.screen.notification.comment.NotificationCommentPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoticesViewModel @Inject constructor(
    private val messageRepository: MessageRepository
): ViewModel() {

    val list = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false, initialLoadSize = 20),
        pagingSourceFactory = {
            NoticePagingSource(messageRepository)
        }
    ).flow.cachedIn(viewModelScope)
}