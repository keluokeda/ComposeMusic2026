package com.ke.music.app.ui.screen.user_status

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.ke.music.app.data.model.MineVO
import com.ke.music.app.ui.components.LoadingView
import com.ke.music.app.ui.components.ProgressDialog
import com.ke.music.app.ui.components.RetryView

@Composable
fun UserStatusRoute(onBack: () -> Unit) {
    val viewModel = hiltViewModel<UserStatusViewModel>()

    UserStatusScreen(
        uiState = viewModel.uiState,
        isRefreshing = viewModel.isRefreshing,
        refresh = {
            viewModel.refresh()
        },
        onBack = onBack,
        onStatusClick = viewModel::commit
    )

    if (viewModel.loading) {
        ProgressDialog()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserStatusScreen(
    uiState: UserStatusViewModel.UiState,
    isRefreshing: Boolean,
    refresh: () -> Unit,
    onBack: () -> Unit,
    onStatusClick: (MineVO.UserSupportStatus) -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(
            title = {
                when (uiState) {
                    UserStatusViewModel.UiState.Error -> Text("我的状态")
                    UserStatusViewModel.UiState.Loading -> Text("我的状态")
                    is UserStatusViewModel.UiState.Success -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("我的状态")
                            val current = uiState.content.current
                            if (current != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                AsyncImage(
                                    model = current.iconUrl,
                                    null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))

                                Text(current.content)
                            }
                        }
                    }
                }

            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }
            }
        )
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                UserStatusViewModel.UiState.Error -> RetryView(refresh)
                UserStatusViewModel.UiState.Loading -> LoadingView()
                is UserStatusViewModel.UiState.Success -> {
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = refresh,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {



                            val sameUser = uiState.sameUser
                            if (sameUser == null) {
                                LazyVerticalGrid(columns = GridCells.Adaptive(100.dp)) {
                                    items(uiState.content.list) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clickable(true) {
                                                    onStatusClick(it)
                                                }
                                                .padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            AsyncImage(
                                                model = it.iconUrl,
                                                contentDescription = null,
                                                modifier = Modifier.size(40.dp)
                                            )
                                            Text(it.content)
                                        }
                                    }
                                }
                            } else {
                                ListItem(headlineContent = {
                                    Text(sameUser.rcmdTitle)
                                })
                                LazyVerticalGrid(columns = GridCells.Adaptive(100.dp)) {
                                    items(sameUser.userRcmdInfoVOList) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable(enabled = true) {

                                                }
                                                .padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            AsyncImage(
                                                model = it.avatarUrl,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(64.dp)
                                                    .clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )

                                            Text(it.nickName, maxLines = 1)

                                        }
                                    }
                                }
                            }


                        }
                    }
                }
            }
        }
    }
}