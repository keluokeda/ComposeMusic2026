package com.ke.music.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BaseVO<T>(
    val code: Int,
    val success: Boolean,
    val message: String,
    val data: T? = null
)

@Serializable
data class BaseListVO<T>(
    val code: Int,
    val success: Boolean,
    val message: String,
    val data: List<T>,
    val hasMore: Boolean? = null,
    val cursor: Long? = null
)

@Serializable
data class User(
    val userId: Long,
    val nickname: String,
    val avatarUrl: String,
    val followed: Boolean,
    val signature: String? = null
)

@Serializable
data class NotificationComment(
    val commentId: Long,
    val user: User,
    val beRepliedUser: User?,
    val content: String,
    val time: Long,
    val beRepliedContent: String? = null,
    val resource: NotificationCommentResourceJson
)

@Serializable
data class NotificationNoticeResponse(
    val id: Long,
    val userId: Long,
    val time: Long,
    val type: Int,
    val noticeJson: Notice
) {
    @Serializable
    data class Notice(
        val user: User,
        val subTitle: String
    )
}

@Serializable
data class NotificationCommentResourceJson(
    val resourceType: Int? = null,
    val resourceSpecialType: Int? = null,
    val id: Long,
    val userId: Long,
    val name: String,
    val imgUrl: String? = null
)

@Serializable
data class Playlist(
    val id: Long,
    val creator: User,
    val coverImgUrl: String,
    val name: String,
    val tags: List<String>,
    val description: String? = null,
    val trackCount: Long,
    val playCount: Long,
    val subscribers: List<User> = emptyList(),
    val updateTime: Long
)

@Serializable
data class Song(
    val id: Long,
    val name: String,
    val album: Album,
    val artists: List<Artist>,
    val mv: Long
) {

    val subTitle: String
        get() = "${artists.joinToString("/") { it.name }} - ${album.name}"
}

@Serializable
data class Album(
    val id: Long,
    val name: String,
    val imageUrl: String
)

@Serializable
data class Artist(
    val id: Long,
    val name: String,
    val avatar: String? = null
)

@Serializable
data class UserDetailVO(
    val id: Long,
    val nickname: String,
    val avatarUrl: String,
    val backgroundUrl: String,
    val level: Int,
    val listenSongs: Int,
    val gender: Int,
    val birthday: Long,
    val provinceName: String,
    val cityName: String,
    val signature: String? = null,
    val followeds: Int,
    val follows: Int,
    val createTime: Long,
    val createDays: Int,
    val playlistBeSubscribedCount: Int,
    val eventCount: Int,
    val age: String,
    val userPlaylists: List<Playlist> = emptyList(),
    val followedPlaylists: List<Playlist> = emptyList()
)

@Serializable
data class SongDetailVO(
    val song: Song,
    val url: String? = null
)

@Serializable
data class PlaylistDetailVO(
    val playlist: Playlist,
    val songs: List<Song>,
    val dynamic: PlaylistDynamicResponse,
    val canBook: Boolean
)

@Serializable
data class PlaylistDynamicResponse(
    val shareCount: Long,
    val playCount: Long,
    val bookedCount: Long,
    val subscribed: Boolean,
    val commentCount: Long
)

@Serializable
data class MvDetailVO(
    val id: Long,
    val name: String,
    val artists: List<Artist>,
    val url: String,
    val cover: String,
    val playCount: Int,
    val subCount: Int,
    val shareCount: Int,
    val commentCount: Int,
    val simiMvs: List<MvVO> = emptyList()
)

@Serializable
data class MvVO(
    val id: Long,
    val name: String,
    val cover: String
)

@Serializable
data class PrivateMessageVO(
    val id: Long,
    val targetUserId: Long,
    val targetNickname: String,
    val targetUserAvatar: String,
    val date: String,
    val content: String
)

@Serializable
data class LoginVO(
    val token: String,
    val id: String,
    val roles: List<String>
)

@Serializable
data class CreateLoginKeyVO(
    val key: String,
    val url: String
)

@Serializable
data class CommentVO(
    val commentId: Long,
    val user: User,
    val content: String? = null,
    val timeString: String,
    val time: Long,
    val likedCount: Int,
    val ipLocation: String,
    val owner: Boolean,
    val liked: Boolean,
    val parentCommentId: Long,
    val replyCount: Int
)

@Serializable
data class ArtistDetailVO(
    val artist: Artist,
    val hotSongs: List<Song>,
    val desc: ArtistDescResponse,
    val simiArtists: List<Artist>,
    val mvs: List<MvVO>,
    val hotAlbums: List<Album>
)

@Serializable
data class ArtistDescResponse(
    val briefDesc: String,
    val introduction: List<ArtistIntroduce>,
    val code: Int
)

@Serializable
data class ArtistIntroduce(
    val ti: String,
    val txt: String
)

@Serializable
data class AlbumDetailVO(
    val id: Long,
    val name: String,
    val picUrl: String,
    val description: String? = null,
    val artists: List<Artist>,
    val songs: List<Song>,
    val isSub: Boolean,
    val image: String
)
