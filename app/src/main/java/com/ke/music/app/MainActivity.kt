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
import com.ke.music.app.data.model.Song
import com.ke.music.app.player.IPlayer
import com.ke.music.app.player.IPlayerController
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
import com.ke.music.app.ui.screen.player.PlayerRoute
import com.ke.music.app.ui.screen.playlist.PlaylistDetailRoute
import com.ke.music.app.ui.screen.playlist.PlaylistDetailViewModel
import com.ke.music.app.ui.screen.recent.RecentRoute
import com.ke.music.app.ui.screen.splash.SplashRoute
import com.ke.music.app.ui.screen.top_playlists.TopPlaylistsRoute
import com.ke.music.app.ui.screen.user_status.UserStatusRoute
import com.ke.music.app.ui.theme.MusicTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
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
                            PlayerRoute(musicViewModel, {
                                controller.removeLastOrNull()
                            }, {
                                controller.add(it)
                            })
                        }

                        entry<Destination.Recent> {
                            RecentRoute(onBack = { controller.removeLastOrNull() }, musicViewModel)
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
    private val player: IPlayer
) : ViewModel(), IPlayerController {


    val currentLrc = player.currentLrc
    val currentSongLiked = player.currentSongLiked
    val isPlaying = player.isPlaying
    val duration = player.duration
    val currentMetadata = player.currentMetadata
    val currentIndex = player.currentIndex

    val repeatMode = player.repeatMode

    val hasMediaItem = player.hasMediaItem

    fun getCurrentPosition() = player.getCurrentPosition()

    val songs = player.songs

    override fun seekTo(positionMs: Long) = player.seekTo(positionMs)

    override fun toggleRepeatMode() = player.toggleRepeatMode()

    override fun skipToNext() = player.skipToNext()
    override fun skipToPrevious() = player.skipToPrevious()
    override fun pause() = player.pause()
    override fun resume() = player.resume()


    override fun playSongs(songs: List<Song>, startPosition: Int) =
        player.playSongs(songs, startPosition)


    override fun playAtIndex(index: Int) {
        player.playAtIndex(index)
    }


    override fun insertIntoCurrentPlaylist(song: Song, playNow: Boolean): Boolean {
        return player.insertIntoCurrentPlaylist(song, playNow)
    }


    fun toggleLike() = player.toggleLike()


    init {
        player.bindScope(viewModelScope)
    }

    init {
//        player.addListener(object : IPlayerListener {
//            override fun onMediaItemTransition(index: Int) {
//                fetchAndPlay(index)
//            }
//
//            override fun onPlayerError(index: Int) {
//                fetchAndPlay(index)
//            }
//        })
    }
}
