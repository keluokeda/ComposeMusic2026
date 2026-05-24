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
}
