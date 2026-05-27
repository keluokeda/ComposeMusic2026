package com.ke.music.app.ui.screen.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ke.music.app.data.model.Mock
import com.ke.music.app.data.model.Mock.dynamic
import com.ke.music.app.data.model.Mock.playlist
import com.ke.music.app.data.model.Mock.songs
import com.ke.music.app.data.model.PlaylistDetailVO
import com.ke.music.app.format
import com.ke.music.app.ui.components.LoadingView
import com.ke.music.app.ui.components.RetryView
import com.ke.music.app.ui.components.ScreenSize
import com.ke.music.app.ui.components.screenSize
import com.ke.music.app.ui.navigation.Destination
import com.ke.music.app.ui.theme.MusicTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailRoute(
    viewModel: PlaylistDetailViewModel,
    onBack: () -> Unit,
    navigate: (Destination) -> Unit
) {
    var description by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    PlaylistDetailScreen(uiState = viewModel.uiState, retry = {
        viewModel.loadDetail()
    }, onBack = onBack, clickDescription = {
        description = it
    }, toComments = {
        navigate(Destination.Comments(2, viewModel.id))
    })

    if (description != null) {
        AlertDialog(onDismissRequest = {
            description = null
        }, confirmButton = {
            TextButton(onClick = {
                description = null
            }) { Text("确定") }
        }, title = {
            Text("歌单介绍")
        }, text = {
            LazyColumn {
                item { Text(description ?: "") }

            }
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistDetailScreen(
    uiState: PlaylistDetailViewModel.UiState,
    onBack: () -> Unit = {},
    share: () -> Unit = {},
    toComments: () -> Unit = {},
    clickBook: () -> Unit = {},
    retry: () -> Unit = {},
    clickDescription: (String) -> Unit = {}
) {

    when (uiState) {
        PlaylistDetailViewModel.UiState.Error -> Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("歌单详情")
                    }, navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                RetryView(retry)
            }
        }

        PlaylistDetailViewModel.UiState.Loading -> Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("歌单详情")
                    }, navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LoadingView()
            }
        }

        is PlaylistDetailViewModel.UiState.Success -> {
            Scaffold(
                topBar =
                    {
                        TopAppBar(
                            title = {
                                Text(uiState.response.playlist.name)
                            },
                            navigationIcon = {
                                IconButton(onClick = onBack) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                                }
                            },
                            actions = {


                            }
                        )
                    }
            ) { paddingValues ->
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    val screenSize = maxWidth.screenSize()

                    if (screenSize == ScreenSize.Compact) {
                        PlaylistDetailCompact(
                            uiState,
                            clickDescription = clickDescription,
                            toComments = toComments
                        )
                    } else {
                        PlaylistDetailMedium(
                            uiState,
                            clickDescription = clickDescription,
                            toComments = toComments
                        )
                    }

                }
            }
        }
    }


}

private val iconBuilder: @Composable (ImageVector) -> Unit = {
    Icon(it, null, modifier = Modifier.size(18.dp))
    Spacer(modifier = Modifier.width(4.dp))
}

@Preview(device = Devices.FOLDABLE)
@Composable
private fun PlaylistDetailMediumPreview() {
    MusicTheme(

    ) {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                PlaylistDetailMedium(uiState = successState)
            }
        }
    }
}

@Composable
private fun PlaylistDetailMedium(
    uiState: PlaylistDetailViewModel.UiState.Success,
    clickDescription: (String) -> Unit = {},
    toComments: () -> Unit = {}
) {

    Row(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AsyncImage(
                    model = uiState.response.playlist.coverImgUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(color = Color.Gray)
                        .clip(RoundedCornerShape(4.dp))
                )

            }
            item {
                val user = uiState.response.playlist.creator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color = Color.Gray)

                    )
                    Text(user.nickname, style = MaterialTheme.typography.titleLarge)

                }
            }

            val description = uiState.response.playlist.description

            if (description != null) {
                item {
                    Text(description, maxLines = 3, modifier = Modifier.clickable(true) {
                        clickDescription(description)
                    })
                }
            }
            val dynamic = uiState.response.dynamic

            item {
                OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    iconBuilder(Icons.Default.Share)
                    Text(dynamic.shareCount.toInt().format("分享"))
                }
            }

            item {
                OutlinedButton(onClick = toComments, modifier = Modifier.fillMaxWidth()) {
                    iconBuilder(Icons.AutoMirrored.Filled.Comment)
                    Text(dynamic.commentCount.toInt().format("评论"))
                }
            }

            item {
                OutlinedButton(
                    enabled = uiState.response.canBook,
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    iconBuilder(if (dynamic.subscribed) Icons.Default.Book else Icons.Outlined.Book)
                    Text(dynamic.shareCount.toInt().format("收藏"))
                }
            }


        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            items(uiState.response.songs, {
                it.id
            }) {
                ListItem(
                    headlineContent = {
                        Text(it.name, maxLines = 1)
                    },
                    trailingContent = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.MoreVert, null)
                        }
                    },
                    leadingContent = {
                        AsyncImage(
                            model = it.album.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                    },
                    supportingContent = {
                        Text(it.subTitle, maxLines = 1)
                    }
                )
            }
        }
    }
}

@Composable
private fun PlaylistDetailCompact(
    uiState: PlaylistDetailViewModel.UiState.Success,
    clickDescription: (String) -> Unit = {},
    toComments: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .height(100.dp),
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(size = 4.dp))
                        .background(Color.Gray),
                    model = uiState.response.playlist.coverImgUrl,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AsyncImage(
                            model = uiState.response.playlist.creator.avatarUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                        )
                        Text(uiState.response.playlist.creator.nickname, maxLines = 1)
                    }

                    Text(
                        uiState.response.dynamic.playCount.toInt().format() + "次播放",
                        style = MaterialTheme.typography.bodySmall
                    )

                    val description = uiState.response.playlist.description

                    if (description != null) {
                        Text(
                            description,
                            maxLines = 2,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.clickable(true) {
                                clickDescription(description)
                            })
                    }

                }

            }


        }


        item {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {


                OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) {
                    iconBuilder(Icons.Default.Share)
                    Text(
                        if (uiState.response.dynamic.shareCount == 0L) "分享" else uiState.response.dynamic.shareCount.toInt()
                            .format()
                    )
                }

                OutlinedButton(onClick = toComments, modifier = Modifier.weight(1f)) {
                    iconBuilder(Icons.AutoMirrored.Filled.Comment)
                    Text(
                        if (uiState.response.dynamic.commentCount == 0L) "评论" else uiState.response.dynamic.commentCount.toInt()
                            .format()
                    )
                }



                OutlinedButton(
                    enabled = uiState.response.canBook,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                ) {
                    iconBuilder(if (uiState.response.dynamic.subscribed) Icons.Default.Book else Icons.Outlined.Book)
                    Text(
                        if (uiState.response.dynamic.bookedCount == 0L) "收藏" else uiState.response.dynamic.bookedCount.toInt()
                            .format()
                    )
                }
            }
        }

        items(uiState.response.songs, {
            it.id
        }) {
            ListItem(
                headlineContent = {
                    Text(it.name, maxLines = 1)
                },
                trailingContent = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, null)
                    }
                },
                leadingContent = {
                    AsyncImage(
                        model = it.album.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                },
                supportingContent = {
                    Text(it.subTitle, maxLines = 1)
                }
            )
        }
    }
}

private val successState = PlaylistDetailViewModel.UiState.Success(
    PlaylistDetailVO(playlist, songs, dynamic, true)
)

@PreviewLightDark
@Composable
private fun PlaylistDetailCompactPreview() {


    MusicTheme {

        Scaffold {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                PlaylistDetailCompact(
                    uiState = successState,

                    )
            }
        }
    }
}
