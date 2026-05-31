package com.ke.music.app.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Destination : NavKey {
    @Serializable
    data object Login : Destination

    @Serializable
    data class Main(val userId: Long) : Destination

    @Serializable
    data class UserDetail(val id: Long) : Destination

    @Serializable
    data class PlaylistDetail(val id: Long) : Destination

    @Serializable
    data object Splash : Destination

    @Serializable
    data class Comments(val type: Int, val id: Long) : Destination

    @Serializable
    data object NotificationComments : Destination

    @Serializable
    data object Notices : Destination

    @Serializable
    data object UserStatus : Destination

    @Serializable
    data object TopPlaylists: Destination
}
