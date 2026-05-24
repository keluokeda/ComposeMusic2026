package com.ke.music.app.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.ke.music.app.data.model.Playlist
import com.ke.music.app.data.model.User
import com.ke.music.app.ui.components.ScreenSize
import com.ke.music.app.ui.components.screenSize
import com.ke.music.app.ui.theme.MusicTheme

@Composable
fun HomeScreen(
    onPlaylistClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val playlists = viewModel.playlists
    val isLoading = viewModel.isLoading

    LaunchedEffect(Unit) {
        viewModel.loadPlaylists()
    }

    HomeScreenContent(
        playlists = playlists,
        isLoading = isLoading,
        onPlaylistClick = onPlaylistClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    playlists: List<Playlist>,
    isLoading: Boolean,
    onPlaylistClick: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("我的歌单") })
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val screenSize = maxWidth.screenSize()
            val isExpanded = screenSize != ScreenSize.Compact

            if (isLoading && playlists.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else {
                if (isExpanded) {
                    // 折叠屏或平板展示网格
                    val columns = if (screenSize == ScreenSize.Medium) 3 else 4
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(playlists) { playlist ->
                            PlaylistGridItem(playlist, onPlaylistClick)
                        }
                    }
                } else {
                    // 手机展示列表
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(playlists) { playlist ->
                            PlaylistListItem(playlist, onPlaylistClick)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaylistListItem(playlist: Playlist, onClick: (Long) -> Unit) {
    Card(
        onClick = { onClick(playlist.id) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = playlist.coverImgUrl,
                contentDescription = null,
                modifier = Modifier.size(56.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "歌曲数量: ${playlist.trackCount}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun PlaylistGridItem(playlist: Playlist, onClick: (Long) -> Unit) {
    Card(
        onClick = { onClick(playlist.id) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                model = playlist.coverImgUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1
                )
                Text(
                    text = "${playlist.trackCount}首",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private val sampleUser = User(
    userId = 1,
    nickname = "Sample User",
    avatarUrl = "",
    followed = false
)

private val samplePlaylists = listOf(
    Playlist(
        id = 1,
        creator = sampleUser,
        coverImgUrl = "",
        name = "我的收藏",
        tags = listOf("Pop"),
        trackCount = 10,
        playCount = 100,
        updateTime = 0
    ),
    Playlist(
        id = 2,
        creator = sampleUser,
        coverImgUrl = "",
        name = "轻音乐",
        tags = listOf("Relax"),
        trackCount = 20,
        playCount = 200,
        updateTime = 0
    ),
    Playlist(
        id = 3,
        creator = sampleUser,
        coverImgUrl = "",
        name = "摇滚狂欢",
        tags = listOf("Rock"),
        trackCount = 15,
        playCount = 150,
        updateTime = 0
    )
)

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun HomeScreenPreview() {
    MusicTheme {
        HomeScreenContent(
            playlists = samplePlaylists,
            isLoading = false,
            onPlaylistClick = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun HomeScreenLoadingPreview() {
    MusicTheme {
        HomeScreenContent(
            playlists = emptyList(),
            isLoading = true,
            onPlaylistClick = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun PlaylistListItemPreview() {
    MusicTheme {
        PlaylistListItem(
            playlist = samplePlaylists.first(),
            onClick = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun PlaylistGridItemPreview() {
    MusicTheme {
        Box(modifier = Modifier.width(200.dp)) {
            PlaylistGridItem(
                playlist = samplePlaylists.first(),
                onClick = {}
            )
        }
    }
}
