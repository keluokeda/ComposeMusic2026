package com.ke.music.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.ke.music.app.data.repository.SongRepository
import com.ke.music.app.player.MusicPlayer
import com.ke.music.app.ui.navigation.Destination
import com.ke.music.app.ui.screen.artist_detail.ArtistDetailRoute
import com.ke.music.app.ui.screen.artist_detail.ArtistDetailViewModel
import com.ke.music.app.ui.screen.artist_fans.ArtistFansRoute
import com.ke.music.app.ui.screen.artist_fans.ArtistFansViewModel
import com.ke.music.app.ui.screen.artists.ArtistsRoute
import com.ke.music.app.ui.screen.comments.CommentsRoute
import com.ke.music.app.ui.screen.comments.CommentsViewModel
import com.ke.music.app.ui.screen.login.LoginRoute
import com.ke.music.app.ui.screen.main.MainRoute
import com.ke.music.app.ui.screen.notification.comment.NotificationCommentRoute
import com.ke.music.app.ui.screen.notification.notices.NoticesRoute
import com.ke.music.app.ui.screen.playlist.PlaylistDetailRoute
import com.ke.music.app.ui.screen.playlist.PlaylistDetailViewModel
import com.ke.music.app.ui.screen.player.PlayerRoute
import com.ke.music.app.ui.screen.splash.SplashRoute
import com.ke.music.app.ui.screen.top_playlists.TopPlaylistsRoute
import com.ke.music.app.ui.screen.user_status.UserStatusRoute
import com.ke.music.app.ui.theme.MusicTheme
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


//    @Inject
//    lateinit var musicViewModel: MusicViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)
//        Logger.d(musicViewModel.toString())
        setContent {
            MusicTheme {
                val controller = rememberNavBackStack(Destination.Splash)
                val musicViewModel = hiltViewModel<MusicViewModel>()
                NavDisplay(
                    modifier = Modifier.fillMaxSize(),
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
//                            HomeScreen({ playlistId ->
//                                controller.add(Destination.PlaylistDetail(playlistId))
//                            })
                            MainRoute(
                                musicViewModel,
                                navigate = {
                                    controller.add(it)
                                }
                            )
                        }

                        entry<Destination.PlaylistDetail> {
                            val viewModel =
                                hiltViewModel<PlaylistDetailViewModel, PlaylistDetailViewModel.Factory>(
                                    creationCallback = { factory ->
                                        factory.create(it.id)
                                    })
                            PlaylistDetailRoute(viewModel, musicViewModel, onBack = {
                                controller.removeLastOrNull()
                            }, {
                                controller.add(it)
                            })
                        }

                        entry<Destination.Comments> {
                            val viewModel =
                                hiltViewModel<CommentsViewModel, CommentsViewModel.Factory>(
                                    creationCallback = { factory ->
                                        factory.create(it.type, it.id)
                                    }
                                )
                            CommentsRoute(viewModel, { controller.removeLastOrNull() }) { }
                        }

                        entry<Destination.NotificationComments> {
                            NotificationCommentRoute()
                        }

                        entry<Destination.Notices> {
                            NoticesRoute(onBack = { controller.removeLastOrNull() }) { }
                        }

                        entry<Destination.UserStatus> {
                            UserStatusRoute(onBack = {
                                controller.removeLastOrNull()
                            })
                        }

                        entry<Destination.TopPlaylists> {
                            TopPlaylistsRoute(onBack = {
                                controller.removeLastOrNull()
                            }) {
                                controller.add(it)
                            }
                        }

                        entry<Destination.Artists> {
                            ArtistsRoute(onBack = {
                                controller.removeLastOrNull()
                            }) {
                                controller.add(it)
                            }
                        }

                        entry<Destination.ArtistDetail> {
                            val viewModel =
                                hiltViewModel<ArtistDetailViewModel, ArtistDetailViewModel.Factory>(
                                    creationCallback = { factory ->
                                        factory.create(it.id)
                                    }
                                )
                            ArtistDetailRoute(viewModel,musicViewModel, onBack = {
                                controller.removeLastOrNull()
                            }) { destination -> controller.add(destination) }
                        }

                        entry<Destination.ArtistFans> {
                            val viewModel =
                                hiltViewModel<ArtistFansViewModel, ArtistFansViewModel.Factory>(
                                    creationCallback = { factory ->
                                        factory.create(it.id)
                                    }
                                )
                            ArtistFansRoute(viewModel, onBack = {
                                controller.removeLastOrNull()
                            }) { destination ->
                                controller.add(destination)
                            }
                        }

                        entry<Destination.Player> {
                            PlayerRoute(musicViewModel) {
                                controller.removeLastOrNull()
                            }
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

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val songRepository: SongRepository,
    val musicPlayer: MusicPlayer
) : ViewModel() {

    fun playNow(id: Long) {
        viewModelScope.launch {
            val response = songRepository.detail(id)

            if (response.success) {
                val detail = response.data!!
                //使用media3播放歌曲
                if (detail.url != null) {
                    musicPlayer.play(
                        detail.url,
                        detail.song.name,
                        detail.song.artists.joinToString("/") { it.name },
                        detail.song.album.imageUrl
                    )
                } else {
                    MusicApp.toast("播放地址不存在")
                }
            } else {
                MusicApp.toast(response.message)
            }
        }
    }

    fun playSongs(songs: List<com.ke.music.app.data.model.Song>, index: Int = 0) {
        viewModelScope.launch {
            // 这里为了简单，假设所有歌曲已经有了 URL，或者我们需要逐个获取
            // 在实际项目中，通常是后台返回带 URL 的列表，或者使用拦截器处理
            // 这里演示获取第一个 URL 并播放列表（假设其他 URL 逻辑类似）
            val urls = songs.map { "" } // 占位，实际需要获取 URL
            // ... 实际逻辑会更复杂，因为需要异步获取多个 URL
            MusicApp.toast("播放列表功能已准备，需配置 URL 获取逻辑")
        }
    }
}
