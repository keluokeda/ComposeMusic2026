package com.ke.music.app.ui.screen.mine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.ke.music.app.data.model.MineVO
import com.ke.music.app.ui.components.LoadingView
import com.ke.music.app.ui.components.RetryView
import com.ke.music.app.ui.navigation.Destination

@Composable
fun MineRoute(navigate: (Destination) -> Unit) {
    val viewModel = hiltViewModel<MineViewModel>()
    MineScreen(
        uiState = viewModel.uiState,
        isRefreshing = viewModel.isRefreshing,
        navigate = navigate,
        refresh = { viewModel.refresh() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MineScreen(
    uiState: MineViewModel.UiState,
    isRefreshing: Boolean,
    navigate: (Destination) -> Unit,
    refresh: () -> Unit
) {
    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues(bottom = padding.calculateBottomPadding()))
        ) {
            when (uiState) {
                MineViewModel.UiState.Loading -> LoadingView()
                MineViewModel.UiState.Error -> RetryView(refresh)
                is MineViewModel.UiState.Success -> {
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = refresh,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        MineContent(uiState.content, navigate)
                    }
                }
            }
        }
    }
}

@Composable
private fun MineContent(content: MineVO, navigate: (Destination) -> Unit) {
    val profile = content.userDetail.profile
    val levelData = content.userLevel.data

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            UserHeader(
                avatarUrl = profile.avatarUrl,
                backgroundUrl = profile.backgroundUrl,
                nickname = profile.nickname,
                signature = profile.signature,
                status = content.currentStatus,
                onClick = { },
                onClickStatus = {
                    navigate(Destination.UserStatus)
                }
            )
        }



        item {
            UserLevelCard(levelData)
        }

        item {
            Card {
                ListItem(
                    modifier = Modifier.clickable(enabled = true) {

                    },
                    headlineContent = {
                        Text("我的关注")
                    },
                    leadingContent = {
                        Icon(Icons.Default.PeopleAlt, null)
                    }, trailingContent = {
                        if (content.userDetail.profile.follows != 0) {
                            Text(content.userDetail.profile.follows.toString())
                        }

                    }
                )

                ListItem(
                    modifier = Modifier.clickable(enabled = true) {

                    },
                    headlineContent = {
                        Text("我的粉丝")
                    },
                    leadingContent = {
                        Icon(Icons.Default.PeopleOutline, null)
                    }, trailingContent = {
                        if (content.userDetail.profile.followeds != 0) {
                            Text(content.userDetail.profile.followeds.toString())
                        }

                    }
                )

                ListItem(
                    modifier = Modifier.clickable(enabled = true) {

                    },
                    headlineContent = {
                        Text("我的云盘")
                    },
                    leadingContent = {
                        Icon(Icons.Default.Cloud, null)
                    }
                )

                ListItem(
                    modifier = Modifier.clickable(enabled = true) {

                    },
                    headlineContent = {
                        Text("最近播放")
                    },
                    leadingContent = {
                        Icon(Icons.Default.History, null)
                    }
                )

                ListItem(
                    modifier = Modifier.clickable(enabled = true) {

                    },
                    headlineContent = {
                        Text("设置")
                    },
                    leadingContent = {
                        Icon(Icons.Default.Settings, null)
                    }
                )
            }
        }

//        if (content.supportStatusList.isNotEmpty()) {
//            item {
//                SupportStatusRow(content.supportStatusList)
//            }
//        }
    }
}

@Composable
private fun UserHeader(
    avatarUrl: String,
    backgroundUrl: String,
    nickname: String,
    signature: String,
    status: MineVO.UserSupportStatus?,
    onClick: () -> Unit,
    onClickStatus: (MineVO.UserSupportStatus?) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = backgroundUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = nickname,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (signature.isNotBlank()) {
                    Text(
                        text = signature,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            TextButton(onClick = {
                onClickStatus(status)
            }) {
                if (status != null) {
                    AsyncImage(
                        model = status.iconUrl,
                        null,
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                }



                Text(status?.content ?: "+添加")
            }
        }
    }
}

//@Composable
//private fun UserStats(follows: Int, followeds: Int, playlistCount: Int) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp),
//        horizontalArrangement = Arrangement.SpaceEvenly
//    ) {
//        StatItem(count = follows, label = "关注")
//        StatItem(count = followeds, label = "粉丝")
//        StatItem(count = playlistCount, label = "歌单")
//    }
//}
//
//@Composable
//private fun StatItem(count: Int, label: String) {
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        Text(
//            text = count.toString(),
//            style = MaterialTheme.typography.titleMedium,
//            fontWeight = FontWeight.Bold
//        )
//        Text(
//            text = label,
//            style = MaterialTheme.typography.bodySmall,
//            color = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//    }
//}

@Composable
private fun UserLevelCard(levelData: MineVO.UserLevelResponse.UserLevelData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lv.${levelData.level}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
//                Text(
//                    text = levelData.info,
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { levelData.progress.toFloat() },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "听歌 ${levelData.nowPlayCount}/${levelData.nextPlayCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "登录 ${levelData.nowLoginCount}/${levelData.nextLoginCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
//
//@Composable
//private fun SupportStatusRow(statusList: List<MineVO.UserSupportStatus>) {
//    LazyRow(
//        contentPadding = PaddingValues(horizontal = 16.dp),
//        horizontalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        items(statusList) { status ->
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier.width(80.dp)
//            ) {
//                AsyncImage(
//                    model = status.iconUrl,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(48.dp)
//                        .clip(RoundedCornerShape(8.dp))
//                )
//                Text(
//                    text = status.content,
//                    style = MaterialTheme.typography.bodySmall,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis,
//                    modifier = Modifier.padding(top = 4.dp)
//                )
//            }
//        }
//    }
//}
