package com.ke.music.app.ui.screen.artists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.ke.music.app.data.model.Artist
import com.ke.music.app.ui.navigation.Destination

@Composable
fun ArtistsRoute(
    onBack: () -> Unit,
    navigate: (Destination) -> Unit
) {
    val viewModel = hiltViewModel<ArtistsViewModel>()
    val artists = viewModel.artists.collectAsLazyPagingItems()

    ArtistsScreen(
        artists = artists,
        type = viewModel.artistType,
        area = viewModel.artistArea,
        onBack = onBack,
        onTypeUpdate = {
            viewModel.artistType = it
            artists.refresh()
        },
        onAreaUpdate = {
            viewModel.artistArea = it
            artists.refresh()
        }) {
        // 跳转到歌手详情
//        navigate(Destination.ArtistDetail(it.id))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtistsScreen(
    artists: LazyPagingItems<Artist>,
    type: ArtistsViewModel.ArtistType,
    area: ArtistsViewModel.ArtistArea,
    onBack: () -> Unit,
    onTypeUpdate: (ArtistsViewModel.ArtistType) -> Unit,
    onAreaUpdate: (ArtistsViewModel.ArtistArea) -> Unit,
    onItemClick: (Artist) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("歌手") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ArtistsViewModel.ArtistType.entries) { item ->
                    FilterChip(
                        selected = type == item,
                        onClick = { onTypeUpdate(item) },
                        label = { Text(item.displayName) }
                    )
                }
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ArtistsViewModel.ArtistArea.entries) { item ->
                    FilterChip(
                        selected = area == item,
                        onClick = { onAreaUpdate(item) },
                        label = { Text(item.displayName) }
                    )
                }
            }

            PullToRefreshBox(
                isRefreshing = artists.loadState.refresh is LoadState.Loading,
                onRefresh = { artists.refresh() },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Adaptive(100.dp)
                ) {
                    items(artists.itemCount, key = artists.itemKey { it.id }) { index ->
                        val artist = artists[index]
                        artist?.let {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onItemClick(it)
                                    }.padding(16.dp)
                                ,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AsyncImage(
                                    model = it.avatar,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                )
                                Text(it.name, maxLines = 1)
                            }
                        }
                    }

//                    when (val state = artists.loadState.append) {
//                        is LoadState.Loading -> {
//                            item {
//                                LoadingView()
//                            }
//                        }
//
//                        is LoadState.Error -> {
//                            item {
//                                RetryView {
//                                    artists.retry()
//                                }
//                            }
//                        }
//
//                        else -> {}
//                    }
                }

//                if (artists.loadState.refresh is LoadState.Error) {
//                    RetryView {
//                        artists.refresh()
//                    }
//                } else if (artists.loadState.refresh is LoadState.Loading && artists.itemCount == 0) {
//                    LoadingView()
//                }
            }
        }
    }
}
