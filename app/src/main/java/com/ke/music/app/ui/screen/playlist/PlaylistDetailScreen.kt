package com.ke.music.app.ui.screen.playlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    id: Long,
    onBack: () -> Unit,
    viewModel: PlaylistDetailViewModel = hiltViewModel()
) {
    val detail = viewModel.detail
    val isLoading = viewModel.isLoading

    LaunchedEffect(id) {
        viewModel.loadDetail(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(detail?.playlist?.name ?: "歌单详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading && detail == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
            }
        } else if (detail != null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(text = detail.playlist.description ?: "", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "歌曲列表", style = MaterialTheme.typography.titleLarge)
                }
                items(detail.songs) { song ->
                    ListItem(
                        headlineContent = { Text(song.name) },
                        supportingContent = { Text(song.artists.joinToString { it.name }) }
                    )
                }
            }
        }
    }
}
