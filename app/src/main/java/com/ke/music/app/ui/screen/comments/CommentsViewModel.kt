package com.ke.music.app.ui.screen.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ke.music.app.data.repository.CommentRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = CommentsViewModel.Factory::class)
class CommentsViewModel @AssistedInject constructor(
    @Assisted type: Int,
    @Assisted id: Long,
    private val commentRepository: CommentRepository
) : ViewModel() {

    val comments = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false, initialLoadSize = 20),
        pagingSourceFactory = {
            CommentPagingSource(type, id, commentRepository)
        }
    ).flow.cachedIn(viewModelScope)

    @AssistedFactory
    interface Factory {
        fun create(
            type: Int,
            id: Long
        ): CommentsViewModel
    }
}