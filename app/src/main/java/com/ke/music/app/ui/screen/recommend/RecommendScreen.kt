package com.ke.music.app.ui.screen.recommend

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.ke.music.app.data.model.RecommendVO
import com.ke.music.app.ui.components.LoadingView
import com.ke.music.app.ui.components.RetryView
import com.ke.music.app.ui.navigation.Destination

@Composable
fun RecommendRoute(
    navigate: (Destination) -> Unit
) {

    val viewModel = hiltViewModel<RecommendViewModel>()
    val uiState = viewModel.uiState

    RecommendScreen(
        uiState = uiState,
        isRefreshing = viewModel.isRefreshing,
        navigate = navigate,
        refresh = { viewModel.refresh() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecommendScreen(
    uiState: RecommendViewModel.UiState,
    isRefreshing: Boolean,
    navigate: (Destination) -> Unit,
    refresh: () -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("推荐") })
    }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                RecommendViewModel.UiState.Loading -> LoadingView()
                RecommendViewModel.UiState.Error -> RetryView(refresh)
                is RecommendViewModel.UiState.Success -> {
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = refresh,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        RecommendContent(uiState.content, navigate)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendContent(
    content: RecommendVO,
    navigate: (Destination) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        if (content.playlists.isNotEmpty()) {
            item {
                SectionTitle("推荐歌单") {
                    TextButton(onClick = {
                        navigate(Destination.TopPlaylists)
                    }) {
                        Text("全部歌单")
                    }
                }
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(content.playlists) { playlist ->
                        RecommendItem(
                            title = playlist.name,
                            imageUrl = playlist.picUrl,
                            onClick = {
                                navigate(Destination.PlaylistDetail(playlist.id))
                            }
                        )
                    }
                }
            }
        }

        if (content.toplistItems.isNotEmpty()) {
            item {
                SectionTitle("排行榜")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(content.toplistItems) { item ->
                        RecommendItem(
                            title = null,
                            imageUrl = item.coverImgUrl,
                            onClick = {
                                navigate(Destination.PlaylistDetail(item.id))
                            }
                        )
                    }
                }
            }
        }

        if (content.newSongs.isNotEmpty()) {
            item {
                SectionTitle("最新歌曲")
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(3),
                    modifier = Modifier
                        .height(184.dp)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(content.newSongs) { item ->
                        RecommendSongItem(
                            title = item.name,
                            subtitle = item.song.artists.joinToString("/") { it.name },
                            imageUrl = item.picUrl,
                            onClick = {

                            }
                        )
                    }
                }
            }
        }

        if (content.songs.isNotEmpty()) {
            item {
                SectionTitle("推荐歌曲") {
                    TextButton(onClick = {}) {
                        Text("历史推荐")
                    }
                }
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(3),
                    modifier = Modifier
                        .height(184.dp)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(content.songs) { song ->
                        RecommendSongItem(
                            title = song.name,
                            subtitle = song.artists.joinToString("/") { it.name },
                            imageUrl = song.album.imageUrl,
                            onClick = {

                            }
                        )
                    }
                }
            }
        }

        if (content.topArtists.isNotEmpty()) {
            item {
                SectionTitle("热门歌手") {
                    TextButton(onClick = {}) {
                        Text("全部歌手")
                    }
                }
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                    items(content.topArtists) {
                        Column(
                            modifier = Modifier
                                .width(100.dp)
                                .clickable(enabled = true) {

                                }
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = it.avatar,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(
                                        CircleShape
                                    )
                            )
                            Text(it.name, maxLines = 1)
                        }
                    }
                }
            }
        }

        if (content.videos.isNotEmpty()) {
            item {
                SectionTitle("推荐视频") {
                    TextButton(onClick = {}) {
                        Text("全部MV")
                    }
                }
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(content.videos) { item ->
                        RecommendItem(
                            title = item.data.title,
                            imageUrl = item.data.coverUrl,
                            onClick = {

                            },
                            isSquare = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, training: @Composable () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        training()
    }
}

@Composable
private fun RecommendItem(
    title: String?,
    imageUrl: String,
    onClick: () -> Unit,
    isSquare: Boolean = true
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(if (isSquare) 1f else 16 / 9f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        if (title != null)
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
    }
}

@Composable
private fun RecommendSongItem(
    title: String,
    subtitle: String,
    imageUrl: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .width(280.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
