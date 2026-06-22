package com.ke.music.app.ui.screen.artist_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ke.music.app.MusicViewModel
import com.ke.music.app.data.model.Song
import com.ke.music.app.format
import com.ke.music.app.ui.components.LoadingView
import com.ke.music.app.ui.components.RetryView
import com.ke.music.app.ui.components.ScreenSize
import com.ke.music.app.ui.components.screenSize
import com.ke.music.app.ui.navigation.Destination
import kotlin.math.max


private val tabs = listOf("简介", "歌曲", "专辑", "视频", "相似歌手")

@Composable
fun ArtistDetailRoute(
    viewModel: ArtistDetailViewModel,
    musicViewModel: MusicViewModel,
    onBack: () -> Unit,
    navigate: (Destination) -> Unit
) {

    ArtistDetailScreen(
        uiState = viewModel.uiState,
        refresh = viewModel::refresh,
        onBack = onBack,
        selectedTabIndex = viewModel.selectedTabIndex,
        onTabClick = {
            viewModel.selectedTabIndex = it
        },
        navigate = navigate,
        play = {
//            musicViewModel.playNow(it)
            musicViewModel.insertIntoCurrentPlaylist(it,true)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtistDetailScreen(
    uiState: ArtistDetailViewModel.UiState,
    refresh: () -> Unit,
    onBack: () -> Unit,
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit,
    navigate: (Destination) -> Unit,
    play: (Song) -> Unit
) {
    val scrollState = rememberLazyListState()
    val density = LocalDensity.current
    val statusBarHeightPx = WindowInsets.statusBars.getTop(density)
    val appBarHeightPx = with(density) { 64.dp.toPx() }
    val stickyLimitPx = statusBarHeightPx + appBarHeightPx

    val tabsOffset by remember {
        derivedStateOf {
            val layoutInfo = scrollState.layoutInfo
            val tabsItem = layoutInfo.visibleItemsInfo.find { it.key == "tabs_sticky_key" }
            if (tabsItem != null) {
                max(stickyLimitPx, tabsItem.offset.toFloat()).toInt()
            } else {
                if (layoutInfo.visibleItemsInfo.isNotEmpty() && layoutInfo.visibleItemsInfo.first().index > 1) {
                    stickyLimitPx.toInt()
                } else {
                    10000 // Below screen
                }
            }
        }
    }

    val isStuck by remember {
        derivedStateOf {
            tabsOffset <= stickyLimitPx + 1 // Add a small epsilon
        }
    }

    when (uiState) {
        ArtistDetailViewModel.UiState.Error -> Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("歌手详情")
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
                RetryView(retry = refresh)
            }
        }

        ArtistDetailViewModel.UiState.Loading -> Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("歌手详情")
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    },

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

        is ArtistDetailViewModel.UiState.Success -> {

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

                val screenSize = this.maxWidth.screenSize()

                if (screenSize == ScreenSize.Compact) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            state = scrollState,
                            modifier = Modifier
                                .fillMaxSize(),
                            overscrollEffect = null
                        ) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    AsyncImage(
                                        model = uiState.content.detail.artist.cover,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f),
                                        contentScale = ContentScale.Crop
                                    )

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                    ) {
                                        Spacer(modifier = Modifier.height(this@BoxWithConstraints.maxWidth - 32.dp))

                                        Card(modifier = Modifier.fillMaxWidth()) {
                                            Column(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                            ) {
                                                Text(
                                                    uiState.content.artist.name,
                                                    style = MaterialTheme.typography.titleLarge
                                                )

                                                TextButton(onClick = {
                                                    navigate(Destination.ArtistFans(uiState.content.artist.id))
                                                }) {
                                                    Text(uiState.content.fansCount.format() + " 粉丝")
                                                }
                                                val imageDesc =
                                                    uiState.content.detail.identify?.imageDesc

                                                if (imageDesc != null)
                                                    Text(
                                                        imageDesc,
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                            }

                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                }
                            }

                            item(key = "tabs_sticky_key") {
                                Spacer(modifier = Modifier.height(48.dp))
                            }

                            if (selectedTabIndex == 0) {
                                //简介
                                item {
                                    ListItem(headlineContent = {
                                        Text(uiState.content.detail.artist.briefDesc)
                                    })
                                }
                            } else if (selectedTabIndex == 1) {
                                //歌曲
                                items(uiState.content.hotSongs) {
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
                                        },
                                        modifier = Modifier.clickable(true) {
                                            play(it)
                                        }
                                    )
                                }
                            } else if (selectedTabIndex == 2) {
                                //专辑
                                items(uiState.content.hotAlbums) {
                                    ListItem(
                                        headlineContent = {
                                            Text(it.name)
                                        },
                                        leadingContent = {
                                            AsyncImage(
                                                model = it.imageUrl,
                                                contentDescription = null,
                                                modifier = Modifier.size(40.dp)
                                            )
                                        }
                                    )
                                }
                            } else if (selectedTabIndex == 3) {
                                //视频
                                items(uiState.content.mvs) {

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Card(modifier = Modifier.fillMaxWidth()) {
                                            AsyncImage(
                                                model = it.cover,
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(16 / 9f)
                                            )
                                            Text(
                                                it.name,
                                                modifier = Modifier.padding(4.dp),
                                                maxLines = 1
                                            )
                                        }

                                    }
                                }
                            } else if (selectedTabIndex == 4) {
                                //相似歌手
                                items(uiState.content.simiArtists) {
                                    ListItem(
                                        headlineContent = {
                                            Text(it.name)
                                        },
                                        leadingContent = {
                                            AsyncImage(
                                                model = it.avatar ?: "",
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                            )
                                        }, modifier = Modifier.clickable(enabled = true){
                                            navigate(Destination.ArtistDetail(it.id))
                                        }
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isStuck) MaterialTheme.colorScheme.surface else Color.Transparent)
                                .statusBarsPadding()
                        ) {
                            TopAppBar(
                                title = {
                                    Text(if (isStuck) uiState.content.artist.name else "")
                                },
                                navigationIcon = {
                                    IconButton(
                                        onClick = onBack,
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            null, tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color.Transparent
                                )
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    translationY = tabsOffset.toFloat()
                                },
                            color = if (isStuck) MaterialTheme.colorScheme.surface else Color.Transparent,
                        ) {
                            SecondaryScrollableTabRow(
                                selectedTabIndex = selectedTabIndex,
                                modifier = Modifier.fillMaxWidth(),
                                minTabWidth = 64.dp,
                                edgePadding = 0.dp,
                                containerColor = Color.Transparent
                            ) {
                                tabs.forEachIndexed { index, string ->
                                    Tab(selected = selectedTabIndex == index, onClick = {
                                        onTabClick(index)
                                    }) {
                                        Text(
                                            string,
                                            modifier = Modifier.padding(
                                                horizontal = 16.dp,
                                                vertical = 12.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Scaffold(modifier = Modifier.weight(1f), topBar = {
                            TopAppBar(
                                title = {
                                    Text(uiState.content.artist.name)
                                },
                                navigationIcon = {
                                    IconButton(onClick = onBack) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                                    }
                                }
                            )
                        }) { paddingValues ->


                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                            ) {
                                item {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        AsyncImage(
                                            model = uiState.content.detail.artist.cover,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(1f),
                                            contentScale = ContentScale.Crop
                                        )

                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp)
                                        ) {
//                                            Spacer(modifier = Modifier.height(this@BoxWithConstraints.maxWidth - 32.dp))

                                            Card(modifier = Modifier.fillMaxWidth()) {
                                                Column(
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .padding(16.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                ) {
                                                    Text(
                                                        uiState.content.artist.name,
                                                        style = MaterialTheme.typography.titleLarge
                                                    )

                                                    TextButton(onClick = {
                                                        navigate(Destination.ArtistFans(uiState.content.artist.id))

                                                    }) {
                                                        Text(uiState.content.fansCount.format() + " 粉丝")
                                                    }

                                                    val imageDesc =
                                                        uiState.content.detail.identify?.imageDesc

                                                    if (imageDesc != null)
                                                        Text(
                                                            imageDesc,
                                                            style = MaterialTheme.typography.bodySmall
                                                        )
                                                }

                                            }
                                            Spacer(modifier = Modifier.height(16.dp))
                                        }
                                    }
                                }
                            }
                        }

                        Scaffold(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) { paddingValues ->
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                            ) {
                                SecondaryScrollableTabRow(
                                    selectedTabIndex = selectedTabIndex,
                                    modifier = Modifier.fillMaxWidth(),
                                    minTabWidth = 64.dp,
                                    edgePadding = 0.dp,
                                ) {
                                    tabs.forEachIndexed { index, string ->
                                        Tab(selected = selectedTabIndex == index, onClick = {
                                            onTabClick(index)
                                        }) {
                                            Text(
                                                string,
                                                modifier = Modifier.padding(
                                                    horizontal = 16.dp,
                                                    vertical = 12.dp
                                                )
                                            )
                                        }
                                    }
                                }

                                LazyColumn(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                ) {
                                    if (selectedTabIndex == 0) {
                                        //简介
                                        item {
                                            ListItem(headlineContent = {
                                                Text(uiState.content.detail.artist.briefDesc)
                                            })
                                        }
                                    } else if (selectedTabIndex == 1) {
                                        //歌曲
                                        items(uiState.content.hotSongs) {
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
                                                },
                                                modifier = Modifier.clickable(true) {
                                                    play(it)
                                                }
                                            )
                                        }

                                        item {
                                            TextButton(onClick = {}, Modifier.fillMaxWidth()) {
                                                Text("全部歌曲")
                                            }
                                        }
                                    } else if (selectedTabIndex == 2) {
                                        //专辑
                                        items(uiState.content.hotAlbums) {
                                            ListItem(
                                                headlineContent = {
                                                    Text(it.name)
                                                },
                                                leadingContent = {
                                                    AsyncImage(
                                                        model = it.imageUrl,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(40.dp)
                                                    )
                                                }
                                            )
                                        }
                                    } else if (selectedTabIndex == 3) {
                                        //视频
                                        items(uiState.content.mvs) {

                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                            ) {
                                                Card(modifier = Modifier.fillMaxWidth()) {
                                                    AsyncImage(
                                                        model = it.cover,
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .aspectRatio(16 / 9f)
                                                    )
                                                    Text(
                                                        it.name,
                                                        modifier = Modifier.padding(4.dp),
                                                        maxLines = 1
                                                    )
                                                }

                                            }
                                        }
                                    } else if (selectedTabIndex == 4) {
                                        //相似歌手
                                        items(uiState.content.simiArtists) {
                                            ListItem(
                                                headlineContent = {
                                                    Text(it.name)
                                                },
                                                leadingContent = {
                                                    AsyncImage(
                                                        model = it.avatar ?: "",
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .size(40.dp)
                                                            .clip(CircleShape)
                                                    )
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


        }
    }
}