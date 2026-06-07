package com.ke.music.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import com.ke.music.app.player.IPlayer
import com.ke.music.app.player.IPlayerListener
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
                                    }
                                )
                            PlaylistDetailRoute(viewModel, musicViewModel, onBack = {
                                controller.removeLastOrNull()
                            }) {
                                controller.add(it)
                            }
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
                            ArtistDetailRoute(viewModel, musicViewModel, onBack = {
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
    val player: IPlayer
) : ViewModel() {

    var currentLrc by mutableStateOf<String?>(null)
        private set

    var currentSongLiked by mutableStateOf(false)
        private set

    private val fetchingIds = mutableSetOf<Long>()

    // 记录用户最后一次想播放的 index（main thread 同步赋值，不依赖异步 StateFlow）
    private var intendedPlayIndex = -1

    private suspend fun loadLrc(id: Long) {
        currentLrc = null
        val response = songRepository.lrc(id)
        if (response.success) {
            currentLrc = response.data
        }
    }

    fun playSongs(songs: List<com.ke.music.app.data.model.Song>, index: Int = 0) {
        player.playSongs(songs, index)
        fetchAndPlay(index)
    }

    fun playAtIndex(index: Int) {
        player.playAtIndex(index)
        if (!player.hasSongUrl(index)) {
            fetchAndPlay(index)
        }
    }

    fun toggleLike() {
        val index = player.currentIndex.value
        val song = player.songs.getOrNull(index) ?: return
        val targetLike = !currentSongLiked

        viewModelScope.launch {
            val response = songRepository.likeSong(song.id, targetLike)
            if (response.success) {
                currentSongLiked = targetLike
            } else {
                MusicApp.toast(response.message.ifEmpty { "操作失败" })
            }
        }
    }

    private fun fetchAndPlay(index: Int) {
        val song = player.songs.getOrNull(index) ?: return
        if (player.hasSongUrl(index)) return
        if (fetchingIds.contains(song.id)) return
        fetchingIds.add(song.id)
        // 在 main thread 上同步记录当前意图，避免依赖异步 StateFlow 判断
        intendedPlayIndex = index

        viewModelScope.launch {
            try {
                loadLrc(song.id)
                val response = songRepository.detail(song.id)
                if (response.success && response.data?.url != null) {
                    currentSongLiked = response.data.liked
                    player.updateSongUrl(index, response.data.url) {
                        // 只有用户没有切换到别的歌时才播放
                        if (intendedPlayIndex == index) {
                            player.startPlayback()
                        }
                    }
                } else {
                    MusicApp.toast(response.message.ifEmpty { "获取歌曲地址失败" })
                }
            } finally {
                fetchingIds.remove(song.id)
            }
        }
    }

    init {
        player.addListener(object : IPlayerListener {
            override fun onMediaItemTransition(index: Int) {
                fetchAndPlay(index)
            }

            override fun onPlayerError(index: Int) {
                fetchAndPlay(index)
            }
        })
    }
}
