package com.ke.music.app.ui.screen.user

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
fun UserDetailScreen(
    id: Long,
    onBack: () -> Unit,
    onPlaylistClick: (Long) -> Unit,
    viewModel: UserDetailViewModel = hiltViewModel()
) {
    val detail = viewModel.detail
    val isLoading = viewModel.isLoading

    LaunchedEffect(id) {
        viewModel.loadDetail(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(detail?.nickname ?: "用户详情") },
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
                    Text(text = "听歌数量: ${detail.listenSongs}", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "等级: ${detail.level}", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "签名: ${detail.signature ?: "无"}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "创建的歌单", style = MaterialTheme.typography.titleLarge)
                }
                items(detail.userPlaylists) { playlist ->
                    Card(
                        onClick = { onPlaylistClick(playlist.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ListItem(
                            headlineContent = { Text(playlist.name) },
                            supportingContent = { Text("${playlist.trackCount}首") }
                        )
                    }
                }
            }
        }
    }
}
