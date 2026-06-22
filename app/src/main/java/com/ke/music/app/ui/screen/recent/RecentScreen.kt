package com.ke.music.app.ui.screen.recent

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.ke.music.app.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentRoute(
    onBack: () -> Unit,
    musicViewModel: MusicViewModel
) {
    val viewModel = hiltViewModel<RecentViewModel>()
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text("最近播放")
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }
            }
        )
    }) { paddingValues ->
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            items(viewModel.recentSongs, key = {
                it.id
            }) {
                ListItem(
                    headlineContent = {
                        Text(it.name)
                    },
                    supportingContent = {
                        Text(it.album.name)
                    },
                    leadingContent = {
                        AsyncImage(
                            model = it.album.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                )
            }
        }

    }
}