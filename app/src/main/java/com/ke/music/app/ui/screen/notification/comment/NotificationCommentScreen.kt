package com.ke.music.app.ui.screen.notification.comment

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCommentRoute() {
    val viewModel = hiltViewModel<NotificationCommentViewModel>()
    val list = viewModel.list.collectAsLazyPagingItems()

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text("和我有关的评论")
            }
        )
    }) { paddingValues ->
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            items(list.itemCount, key = list.itemKey { it.commentId }) { index ->
                val item = list[index]!!
                ListItem(
                    headlineContent = {
                        Text(item.toString())
                    }
                )
            }
        }
    }
}