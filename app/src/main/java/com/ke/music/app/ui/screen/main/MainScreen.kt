package com.ke.music.app.ui.screen.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import coil3.compose.AsyncImage
import com.ke.music.app.MusicViewModel
import com.ke.music.app.ui.components.ScreenSize
import com.ke.music.app.ui.components.screenSize
import com.ke.music.app.ui.navigation.Destination
import com.ke.music.app.ui.screen.message.MessageRoute
import com.ke.music.app.ui.screen.mine.MineRoute
import com.ke.music.app.ui.screen.my_playlists.MyPlaylistsRoute
import com.ke.music.app.ui.screen.notes.NotesRoute
import com.ke.music.app.ui.screen.recommend.RecommendRoute
import kotlinx.serialization.Serializable

@Serializable
sealed interface MainTab : NavKey {
    @Serializable
    data object Recommend : MainTab

    @Serializable
    data object MyPlaylists : MainTab

    @Serializable
    data object Message : MainTab

    @Serializable
    data object Notes : MainTab

    @Serializable
    data object Mine : MainTab
}


@Composable
fun MainRoute(musicViewModel: MusicViewModel,navigate: (Destination) -> Unit) {
    // 内部导航使用的 BackStack
    val subController = rememberNavBackStack(MainTab.Recommend)
    // 获取当前选中的 tab
    val currentTab = subController.last() as MainTab

//    val musicViewModel = hiltViewModel<MusicViewModel>()

    BoxWithConstraints {
        val screenSize = maxWidth.screenSize()

        if (screenSize == ScreenSize.Compact) {
            // 手机端使用 NavigationBar
            Scaffold(
                bottomBar = {
                    Column {
                        BottomPlayerBar(musicViewModel, navigate)
                        MainNavigationBar(
                            currentTab = currentTab,
                            onTabSelected = { tab ->
                                subController.clear()
                                subController.add(tab)
                            }
                        )
                    }
                }
            ) { padding ->
                MainContent(
                    modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
                    subController = subController,
                    musicViewModel = musicViewModel,
                    navigate = navigate,
                )
            }
        } else {
            // 平板/桌面端使用 NavigationRail
            Row(modifier = Modifier.fillMaxSize()) {
                MainNavigationRail(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                        subController.clear()
                        subController.add(tab)
                    }
                )
                Scaffold(
                    bottomBar = {
                        BottomPlayerBar(musicViewModel, navigate)
                    }
                ) { padding ->
                    MainContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = padding.calculateBottomPadding()),
                        subController = subController,
                        musicViewModel = musicViewModel,
                        navigate = navigate,
//                        playSong = {
//                            musicViewModel.playNow(it)
//                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomPlayerBar(
    musicViewModel: MusicViewModel,
    navigate: (Destination) -> Unit
) {
    val player = musicViewModel.player
    val hasMediaItem by player.hasMediaItem.collectAsState()
    val currentMetadata by player.currentMetadata.collectAsState()
    val isPlaying by player.isPlaying.collectAsState()

    if (!hasMediaItem) return

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navigate(Destination.Player) },
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 4.dp
    ) {
        Column {
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = currentMetadata?.artworkUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentMetadata?.title ?: "未知歌曲",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentMetadata?.artist ?: "未知歌手",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = { player.skipToPrevious() }) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = null)
                }

                IconButton(onClick = {
                    if (isPlaying) player.pause() else player.resume()
                }) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                }

                IconButton(onClick = { player.skipToNext() }) {
                    Icon(Icons.Default.SkipNext, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun MainContent(
    modifier: Modifier = Modifier,
    subController: NavBackStack<out NavKey>,
    musicViewModel: MusicViewModel,
    navigate: (Destination) -> Unit,
) {
    NavDisplay(
        modifier = modifier,
        backStack = subController,
        entryProvider = entryProvider {
            entry<MainTab.Recommend> {
                RecommendRoute(musicViewModel, navigate)
            }
            entry<MainTab.Message> {
                MessageRoute { destination ->
                    navigate(destination)
                }
            }

            entry<MainTab.MyPlaylists> {
                MyPlaylistsRoute {
                    navigate(Destination.PlaylistDetail(it))
                }
            }

            entry<MainTab.Notes> {
                NotesRoute(navigate)
            }

            entry<MainTab.Mine> {
                MineRoute(navigate)
            }
        }
    )
}

@Composable
private fun MainNavigationBar(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentTab is MainTab.Recommend,
            onClick = { onTabSelected(MainTab.Recommend) },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("首页") }
        )
        NavigationBarItem(
            selected = currentTab is MainTab.Message,
            onClick = { onTabSelected(MainTab.Message) },
            icon = { Icon(Icons.AutoMirrored.Filled.Message, null) },
            label = { Text("消息") }
        )

        NavigationBarItem(
            selected = currentTab is MainTab.MyPlaylists,
            onClick = { onTabSelected(MainTab.MyPlaylists) },
            icon = { Icon(Icons.Default.LibraryMusic, null) },
            label = { Text("歌单") }
        )

        NavigationBarItem(
            selected = currentTab is MainTab.Notes,
            onClick = { onTabSelected(MainTab.Notes) },
            icon = { Icon(Icons.AutoMirrored.Filled.Notes, null) },
            label = { Text("笔记") }
        )

        NavigationBarItem(
            selected = currentTab is MainTab.Mine,
            onClick = { onTabSelected(MainTab.Mine) },
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("我的") }
        )
    }
}

@Composable
private fun MainNavigationRail(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit
) {
    NavigationRail(modifier = Modifier.fillMaxHeight()) {
        NavigationRailItem(
            selected = currentTab is MainTab.Recommend,
            onClick = { onTabSelected(MainTab.Recommend) },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("首页") }
        )
        NavigationRailItem(
            selected = currentTab is MainTab.Message,
            onClick = { onTabSelected(MainTab.Message) },
            icon = { Icon(Icons.AutoMirrored.Filled.Message, null) },
            label = { Text("消息") }
        )

        NavigationRailItem(
            selected = currentTab is MainTab.MyPlaylists,
            onClick = { onTabSelected(MainTab.MyPlaylists) },
            icon = { Icon(Icons.Default.LibraryMusic, null) },
            label = { Text("歌单") }
        )

        NavigationRailItem(
            selected = currentTab is MainTab.Notes,
            onClick = { onTabSelected(MainTab.Notes) },
            icon = { Icon(Icons.AutoMirrored.Filled.Notes, null) },
            label = { Text("笔记") }
        )

        NavigationRailItem(
            selected = currentTab is MainTab.Mine,
            onClick = { onTabSelected(MainTab.Mine) },
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("我的") }
        )
    }
}
