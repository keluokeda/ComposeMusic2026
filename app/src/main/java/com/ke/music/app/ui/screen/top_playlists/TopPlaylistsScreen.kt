package com.ke.music.app.ui.screen.top_playlists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.ke.music.app.data.model.Playlist
import com.ke.music.app.ui.navigation.Destination

@Composable
fun TopPlaylistsRoute(onBack: () -> Unit, navigate: (Destination) -> Unit) {
    val viewModel = hiltViewModel<TopPlaylistsViewModel>()
    val playlists = viewModel.playlists.collectAsLazyPagingItems()

    TopPlaylistsScreen(playlists, viewModel.categoryList, viewModel.category, onBack, {

        viewModel.updateCategory(it)
        playlists.refresh()
    }) {
        navigate(Destination.PlaylistDetail(it))
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopPlaylistsScreen(
    playlists: LazyPagingItems<Playlist>,
    tags: List<String>,
    selectedTag: String?,
    onBack: () -> Unit,
    onTagClick: (String) -> Unit,
    onPlaylistClick: (Long) -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text("精品歌单")
            },
            navigationIcon = {
                IconButton(onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }
            }
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (tags.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tags) {
                        InputChip(
                            selected = selectedTag == it,
                            onClick = {
                                onTagClick(it)
                            }, label = {
                                Text(it)
                            }
                        )
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(100.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(playlists.itemCount, key = playlists.itemKey {
                    it.id
                }) {
                    val playlist = playlists[it]!!
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = true) {
                                onPlaylistClick(playlist.id)
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AsyncImage(
                            model = playlist.coverImgUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)

                        )

                        Text(playlist.name, maxLines = 1)
                    }
                }
            }

        }
    }
}