package com.ke.music.app.ui.screen.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.paging.LoadState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.ke.music.app.ui.navigation.Destination


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesRoute(navigate: (Destination) -> Unit) {
    val viewModel = hiltViewModel<NotesViewModel>()
    val notes = viewModel.notes.collectAsLazyPagingItems()

    val isRefreshing = notes.loadState.refresh is LoadState.Loading

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text("笔记")
            }
        )
    }) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { notes.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            val itemMinWidth = 120.dp
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(itemMinWidth),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 16.dp,
                contentPadding = PaddingValues(16.dp)
            ) {
                items(notes.itemCount) { index ->
                    val item = notes[index]!!
                    Card(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                        if (item.pics.isNotEmpty()) {
                            AsyncImage(
                                model = item.pics.first(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .heightIn(max = itemMinWidth * 12 / 9f),
                                contentScale = ContentScale.Crop

                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.Start
                        ) {

                            if (item.eventJson.title.isNotEmpty()) {
                                Text(item.eventJson.title)
                            }


                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = item.user.avatarUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                )

                                Text(
                                    item.user.nickname,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}