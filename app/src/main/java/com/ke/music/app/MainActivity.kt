package com.ke.music.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.ke.music.app.ui.navigation.Destination
import com.ke.music.app.ui.screen.home.HomeScreen
import com.ke.music.app.ui.screen.login.LoginRoute
import com.ke.music.app.ui.screen.playlist.PlaylistDetailScreen
import com.ke.music.app.ui.screen.splash.SplashRoute
import com.ke.music.app.ui.screen.user.UserDetailScreen
import com.ke.music.app.ui.theme.MusicTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicTheme {
                val controller = rememberNavBackStack(Destination.Splash)

                NavDisplay(
                    backStack = controller,
                    onBack = { if (controller.size > 1) controller.removeLastOrNull() },
                    entryProvider = entryProvider {
                        entry<Destination.Splash> {
                            SplashRoute {
                                controller.clear()
                                controller.add(it)
                            }
                        }

                        entry<Destination.Login> {
                            LoginRoute {
                                controller.clear()
                                controller.add(Destination.Splash)
                            }
                        }

                        entry<Destination.Main> {
                            HomeScreen({ playlistId ->
                                controller.add(Destination.PlaylistDetail(playlistId))
                            })
                        }

                        entry<Destination.PlaylistDetail> {
                            PlaylistDetailScreen(it.id, {
                                controller.removeLastOrNull()
                            })
                        }

                    }, entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        // Then add the view model store decorator
                        rememberViewModelStoreNavEntryDecorator()
                    )
                )
            }
        }
    }
}
