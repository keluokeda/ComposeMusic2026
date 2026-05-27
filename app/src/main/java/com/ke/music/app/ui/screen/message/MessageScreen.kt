package com.ke.music.app.ui.screen.message

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.ke.music.app.ui.components.LoadingView
import com.ke.music.app.ui.components.RetryView
import com.ke.music.app.ui.navigation.Destination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageRoute(
    navigate: (Destination) -> Unit
) {
    val viewModel = hiltViewModel<MessageViewModel>()

    val uiState = viewModel.uiState

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text("消息")
            },
            actions = {
                IconButton(onClick = {
                    navigate(Destination.NotificationComments)
                }) {
                    Icon(Icons.AutoMirrored.Outlined.Comment,null)
                }

                IconButton(onClick = {}) {
                    Icon(Icons.Default.AlternateEmail,null)
                }

                IconButton(onClick = {
                    navigate(Destination.Notices)
                }) {
                    Icon(Icons.Default.NotificationsNone,null)
                }
            }
        )
    }) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            when (uiState) {
                MessageViewModel.UiState.Error -> RetryView { viewModel.refresh() }
                MessageViewModel.UiState.Loading -> LoadingView()
                is MessageViewModel.UiState.Success -> {

                    PullToRefreshBox(
                        isRefreshing = viewModel.isRefreshing,
                        onRefresh = { viewModel.refresh() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(uiState.list) {
                                ListItem(
                                    headlineContent = {
                                        Text(it.targetNickname)
                                    }, supportingContent = {
                                        Text(it.content, maxLines = 1)
                                    },
                                    leadingContent = {
                                        AsyncImage(
                                            model = it.targetUserAvatar,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(
                                                    CircleShape
                                                )
                                        )
                                    },
                                    trailingContent = {
                                        Text(it.date)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }


}