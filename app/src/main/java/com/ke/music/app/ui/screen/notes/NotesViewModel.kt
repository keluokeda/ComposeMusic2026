package com.ke.music.app.ui.screen.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ke.music.app.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {
    val notes = Pager(
        config = PagingConfig(enablePlaceholders = false, initialLoadSize = 20, pageSize = 20)
    ){
        NotesPagingSource(notesRepository)
    }.flow.cachedIn(viewModelScope)
}