package com.ke.music.app.ui.screen.main

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.ke.music.app.ui.components.ScreenSize
import com.ke.music.app.ui.components.screenSize
import com.ke.music.app.ui.navigation.Destination
import com.ke.music.app.ui.screen.message.MessageRoute
import com.ke.music.app.ui.screen.mine.MineRoute
import com.ke.music.app.ui.screen.my_playlists.MyPlaylistsRoute
import com.ke.music.app.ui.screen.recommend.RecommendRoute
import kotlinx.serialization.Serializable

@Serializable
sealed interface MainTab : NavKey {
    @Serializable
    data object Recommend : MainTab

    @Serializable
    data object MyPlaylists: MainTab

    @Serializable
    data object Message : MainTab

    @Serializable
    data object Mine : MainTab
}


@Composable
fun MainRoute(navigate: (Destination) -> Unit) {
    // 内部导航使用的 BackStack
    val subController = rememberNavBackStack(MainTab.Recommend)
    // 获取当前选中的 tab
    val currentTab = subController.last() as MainTab

    BoxWithConstraints {
        val screenSize = maxWidth.screenSize()

        if (screenSize == ScreenSize.Compact) {
            // 手机端使用 NavigationBar
            Scaffold(
                bottomBar = {
                    MainNavigationBar(
                        currentTab = currentTab,
                        onTabSelected = { tab ->
                            subController.clear()
                            subController.add(tab)
                        }
                    )
                }
            ) { padding ->
                MainContent(
                    modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
                    subController = subController,
                    navigate = navigate
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
                MainContent(
                    modifier = Modifier.weight(1f),
                    subController = subController,
                    navigate = navigate
                )
            }
        }
    }
}

@Composable
private fun MainContent(
    modifier: Modifier = Modifier,
    subController: NavBackStack<out NavKey>,
    navigate: (Destination) -> Unit
) {
    NavDisplay(
        modifier = modifier,
        backStack = subController,
        entryProvider = entryProvider {
            entry<MainTab.Recommend> {
                RecommendRoute(navigate)
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
            selected = currentTab is MainTab.Mine,
            onClick = { onTabSelected(MainTab.Mine) },
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("我的") }
        )
    }
}
