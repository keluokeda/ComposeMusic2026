package com.ke.music.app.data.model

import kotlinx.serialization.SerialName
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

//@Serializable
//data class ArtistDetailVO(
//    val artist: Artist,
//    val hotSongs: List<Song>,
//    val desc: ArtistDescResponse,
//    val simiArtists: List<Artist>,
//    val mvs: List<MvVO>,
//    val hotAlbums: List<Album>
//)

@Serializable
data class ArtistDetailVO(
    val artist: Artist,
    val hotSongs: List<Song>,
    val detail: ArtistDetailResponse,
    val simiArtists: List<Artist>,
    val mvs: List<MvVO>,
    val hotAlbums: List<Album>,
    val fansCount: Long,
    val followed: Boolean
) {

    @Serializable
    data class ArtistDetailResponse(
        val videoCount: Int,
        val identify: ArtistIdentify,
        val artist: ArtistDetail,
        val blacklist: Boolean,
        val preferShow: Int,
        val showPriMsg: Boolean,
        val secondaryExpertIdentiy: List<ExpertIdentity>,
    )

    @Serializable
    data class ArtistIdentify(
//        val imageUrl: String,
        val imageDesc: String,
        val actionUrl: String,
    )

    @Serializable
    data class ArtistDetail(
        val id: Long,
        val cover: String,
        val avatar: String,
        val name: String,
        val transNames: List<String>,
        val alias: List<String>,
        val identities: List<String>,
//        val identifyTag: String?,
        val briefDesc: String,
        val rank: ArtistRank?,
        val albumSize: Int,
        val musicSize: Int,
        val mvSize: Int,
    )

    @Serializable
    data class ArtistRank(
        val rank: Int,
        val type: Int,
    )

    @Serializable
    data class ExpertIdentity(
        val expertIdentiyId: Long,
        val expertIdentiyName: String,
        val expertIdentiyCount: Int,
    )

}

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

@Serializable
data class RecommendVO(
    val videos: List<RecommendVideoItem>,
    val playlists: List<RecommendPlaylist>,
    val songs: List<Song>,
    val newSongs: List<NewSongItem>,
    val toplistItems: List<ToplistItem>,
    val topArtists: List<Artist>
) {

    @Serializable
    data class ToplistItem(
        val id: Long,
        val name: String,
        val description: String?,
        val coverImgUrl: String
    )


    @Serializable
    data class NewSongItem(
        val song: NewSong,
        val picUrl: String,
        val canDislike: Boolean,
        val name: String,
        val id: Long,
        val type: Int,
        val alg: String
    )

    @Serializable
    data class NewSong(
        val id: Long,
        val name: String,
        val duration: Int,
        val fee: Int,
        val mvid: Long,
        val no: Int,
        val disc: String,
        val copyright: Int,
        val copyrightId: Long,
        val commentThreadId: String,
        val publishTime: Long,
        val popularity: Int,
        val score: Int,
        val originCoverType: Int,
        val alias: List<String>,
        val artists: List<Artist>,
        val album: NewSongAlbum,
        val privilege: NewSongPrivilege,
        val lMusic: MusicQuality? = null,
        val mMusic: MusicQuality? = null,
        val hMusic: MusicQuality? = null,
        val sqMusic: MusicQuality? = null,
        val hrMusic: MusicQuality? = null,
        val bMusic: MusicQuality? = null,
        val originSongSimpleData: OriginSongSimpleData? = null
    )

    @Serializable
    data class NewSongAlbum(
        val id: Long,
        val name: String,
        val picUrl: String,
        val publishTime: Long,
        val type: String,
        val subType: String,
        val company: String = "",
        val size: Int,
        val artist: Artist,
        val artists: List<Artist>
    )

    @Serializable
    data class MusicQuality(
        val id: Long,
        val extension: String,
        val bitrate: Int,
        val size: Long,
        val playTime: Int,
        val sr: Int
    )

    @Serializable
    data class NewSongPrivilege(
        val id: Long,
        val fee: Int,
        val payed: Int,
        val st: Int,
        val pl: Int,
        val dl: Int,
        val sp: Int,
        val cp: Int,
        val subp: Int,
        val cs: Boolean,
        val maxbr: Int,
        val fl: Int,
        val toast: Boolean,
        val flag: Long,
        val preSell: Boolean,
        val plLevel: String,
        val dlLevel: String,
        val flLevel: String,
        val maxBrLevel: String,
        val playMaxbr: Int,
        val playMaxBrLevel: String,
        val downloadMaxbr: Int,
        val downloadMaxBrLevel: String,
        val chargeInfoList: List<ChargeInfo>,
        val freeTrialPrivilege: FreeTrialPrivilege
    )

    @Serializable
    data class ChargeInfo(
        val rate: Int,
        val chargeType: Int
    )

    @Serializable
    data class FreeTrialPrivilege(
        val userConsumable: Boolean,
        val resConsumable: Boolean
    )

    @Serializable
    data class OriginSongSimpleData(
        val name: String,
        val songId: Long,
        val artists: List<OriginSongArtist>,
        val albumMeta: OriginSongAlbumMeta
    )

    @Serializable
    data class OriginSongArtist(
        val id: Long,
        val name: String
    )

    @Serializable
    data class OriginSongAlbumMeta(
        val id: Long,
        val name: String
    )


    @Serializable
    data class RecommendPlaylist(
        val id: Long,
        val type: Int,
        val name: String,
        val copywriter: String,
        val picUrl: String,
        val playcount: Long,
        val createTime: Long,
        val creator: RecommendPlaylistCreator,
        val trackCount: Int,
        val userId: Long,
        val alg: String
    )

    @Serializable
    data class RecommendPlaylistCreator(
        val userId: Long,
        val nickname: String,
        val avatarUrl: String,
        val backgroundUrl: String,
        val avatarImgId: Long,
        val avatarImgIdStr: String,
        val backgroundImgId: Long,
        val backgroundImgIdStr: String,
        val vipType: Int,
        val province: Int,
        val city: Int,
        val birthday: Long,
        val accountStatus: Int,
        val authStatus: Int,
        val userType: Int,
        val gender: Int,
        val detailDescription: String,
        val description: String,
        val signature: String,
        val defaultAvatar: Boolean,
        val expertTags: List<String>? = null,
        val djStatus: Int,
        val followed: Boolean,
        val mutual: Boolean,
        val remarkName: String? = null,
        val authority: Int
    )

    @Serializable
    data class RecommendVideoItem(
        val type: Int,
        val displayed: Boolean,
        val alg: String,
        val extAlg: String? = null,
        val data: VideoData
    )

    @Serializable
    data class VideoData(
        val alg: String,
        val scm: String,
        val threadId: String,
        val coverUrl: String,
        val height: Int,
        val width: Int,
        val title: String,
        val description: String? = null,
        val commentCount: Int,
        val shareCount: Int,
        val resolutions: List<VideoResolution>,
        val creator: VideoCreator,
        val urlInfo: VideoUrlInfo,
        val videoGroup: List<VideoGroup>,
        val previewUrl: String? = null,
        val previewDurationms: Int,
        val hasRelatedGameAd: Boolean,
        val markTypes: List<Int>? = null,
        val relateSong: List<VideoRelateSong>,
        val vid: String,
        val durationms: Long,
        val playTime: Long,
        val praisedCount: Int,
        val praised: Boolean,
        val subscribed: Boolean
    )

    @Serializable
    data class VideoResolution(
        val resolution: Int,
        val size: Long
    )

    @Serializable
    data class VideoCreator(
        val userId: Long,
        val nickname: String,
        val avatarUrl: String,
        val backgroundUrl: String,
        val avatarImgId: Long,
        val avatarImgIdStr: String,
        val backgroundImgId: Long,
        val backgroundImgIdStr: String,
        val vipType: Int,
        val province: Int,
        val city: Int,
        val birthday: Long,
        val accountStatus: Int,
        val authStatus: Int,
        val userType: Int,
        val gender: Int,
        val detailDescription: String,
        val description: String,
        val signature: String,
        val defaultAvatar: Boolean,
        val expertTags: List<String>? = null,
        val experts: Map<String, String>? = null,
        val djStatus: Int,
        val followed: Boolean,
        val mutual: Boolean,
        val remarkName: String? = null,
        val authority: Int
    )

    @Serializable
    data class VideoUrlInfo(
        val id: String,
        val url: String,
        val size: Long,
        val validityTime: Int,
        val needPay: Boolean,
        val r: Int
    )

    @Serializable
    data class VideoGroup(
        val id: Int,
        val name: String,
        val alg: String? = null
    )

    @Serializable
    data class VideoRelateSong(
        val id: Long,
        val name: String,

        val artists: List<Artist>,
        val album: Album,
        val dt: Int,
        val fee: Int,
        val mv: Long,
        val privilege: VideoSongPrivilege? = null
    )

    @Serializable
    data class VideoSongPrivilege(
        val id: Long,
        val fee: Int,
        val payed: Int,
        val st: Int,
        val pl: Int,
        val dl: Int,
        val sp: Int,
        val cp: Int,
        val subp: Int,
        val cs: Boolean,
        val maxbr: Int,
        val fl: Int,
        val toast: Boolean,
        val flag: Long,
        val preSell: Boolean
    )
}

@Serializable
data class MineVO(
    val userDetail: UserDetailResponse,
    val userLevel: UserLevelResponse,
    val userAccount: UserAccountResponse,
//    val supportStatusList: List<UserSupportStatus>,
    val currentStatus: UserSupportStatus?
) {

//    @Serializable
//    data class UserCurrentStatusResponse(
//        val data:UserCurrentStatus
//    ){
//        @Serializable
//        data class UserCurrentStatus(
//            val id: Long? = null,
//            val userId: Long? = null,
//            val status: Int? = null,
//            val content: UserSupportStatus? = null,
//            val editLimit: Boolean? = null
//        ){
//
//            fun toStatus(): UserSupportStatus?{
//
//            }
//        }
//    }

    @Serializable
    data class UserDetailResponse(
        val code: Int,
        val level: Int,
        val listenSongs: Int,
        val createTime: Long,
        val createDays: Int,
        val mobileSign: Boolean,
        val pcSign: Boolean,
        val newUser: Boolean,
        val recallUser: Boolean,
        val adValid: Boolean,
        val peopleCanSeeMyPlayRecord: Boolean,
        val userPoint: UserPoint,
        val profile: Profile,
        val bindings: List<Binding>,
        val profileVillageInfo: ProfileVillageInfo
    ) {
        @Serializable
        data class UserPoint(
            val userId: Long,
            val balance: Int,
            val blockBalance: Int,
            val updateTime: Long,
            val version: Int,
            val status: Int
        )

        @Serializable
        data class Profile(
            val userId: Long,
            val nickname: String,
            val avatarUrl: String,
            val avatarImgId: Long,
            val avatarImgIdStr: String,
            val backgroundUrl: String,
            val backgroundImgId: Long,
            val backgroundImgIdStr: String,
            val signature: String,
            val createTime: Long,
            val birthday: Long,
            val gender: Int,
            val province: Int,
            val city: Int,
            val vipType: Int,
            val djStatus: Int,
            val authStatus: Int,
            val accountStatus: Int,
            val authority: Int,
            val userType: Int,
            val followed: Boolean,
            val mutual: Boolean,
            val followMe: Boolean,
            val defaultAvatar: Boolean,
            val blacklist: Boolean,
            val inBlacklist: Boolean,
            val follows: Int,
            val followeds: Int,
            val newFollows: Int,
            val playlistCount: Int,
            val eventCount: Int,
            val description: String,
            val detailDescription: String,
            val expertTags: List<String>? = null,
            val experts: Map<String, String>? = null,
            val remarkName: String? = null,
            val avatarDetail: String? = null,
            val followTime: Long? = null,
            val privacyItemUnlimit: PrivacyItemUnlimit? = null
        )

        @Serializable
        data class PrivacyItemUnlimit(
            val area: Boolean,
            val college: Boolean,
            val gender: Boolean,
            val age: Boolean,
            val villageAge: Boolean
        )

        @Serializable
        data class Binding(
            val id: Long,
            val userId: Long,
            val type: Int,
            val expiresIn: Long,
            val refreshTime: Long,
            val bindingTime: Long,
            val expired: Boolean,
            val url: String,
            val tokenJsonStr: String? = null
        )

        @Serializable
        data class ProfileVillageInfo(
            val title: String,
            val targetUrl: String,
            val imageUrl: String? = null
        )
    }

    @Serializable
    data class UserLevelResponse(
        val code: Int,
        val full: Boolean,
        val data: UserLevelData
    ) {

        @Serializable
        data class UserLevelData(
            val userId: Long,
            val level: Int,
            val info: String,
            val progress: Double,
            val nowPlayCount: Int,
            val nextPlayCount: Int,
            val nowLoginCount: Int,
            val nextLoginCount: Int
        )
    }


    @Serializable
    data class UserAccountResponse(
        val code: Int,
        val account: UserAccount,
        val profile: UserProfile
    ) {

        @Serializable
        data class UserAccount(
            val id: Long,
            val userName: String,
            val type: Int,
            val status: Int,
            val createTime: Long,
            val vipType: Int,
            val ban: Int,
            val anonimousUser: Boolean,
            val paidFee: Boolean
        )

        @Serializable
        data class UserProfile(
            val userId: Long,
            val userType: Int,
            val nickname: String,
            val avatarUrl: String,
            val avatarImgId: Long,
            val backgroundUrl: String,
            val backgroundImgId: Long,
            val signature: String,
            val createTime: Long,
            val birthday: Long,
            val gender: Int,
            val province: Int,
            val city: Int,
            val vipType: Int,
            val djStatus: Int,
            val authStatus: Int,
            val accountStatus: Int,
            val followed: Boolean,
            val mutual: Boolean,
            val defaultAvatar: Boolean,
            val lastLoginTime: Long,
            val lastLoginIP: String,
            val description: String? = null,
            val detailDescription: String? = null,
            val expertTags: List<String>? = null,
            val experts: Map<String, String>? = null,
            val remarkName: String? = null,
            val avatarDetail: String? = null
        )
    }

    @Serializable
    data class UserSupportStatus(
        val type: String,
        val iconUrl: String,
        val content: String,
        val actionUrl: String? = null
    )
}

@Serializable
data class UserStatusVO(
    val current: MineVO.UserSupportStatus?,
    val list: List<MineVO.UserSupportStatus>
)

@Serializable
data class SameStatusUserResponse(
    val rcmdTitle: String,
    val onlyShowState: Boolean,
    val statusContentDTO: StatusContent,
    val userRcmdInfoVOList: List<UserRcmdInfo>
) {

    @Serializable
    data class StatusContent(
        val type: String,
        val iconUrl: String,
        val content: String,
        val canEdit: Boolean,
        val actionUrl: String? = null,
        val vistorUrl: String? = null,
        val desc: String? = null
    )

    @Serializable
    data class UserRcmdInfo(
        val userId: Long,
        val nickName: String,
        val avatarUrl: String,
        val actionUrl: String,
        val statusContentDTO: StatusContent,
        val tagRcmdVOList: List<TagRcmd>,
        val rcmdWords: String? = null,
//        val identityInfoDTO: String? = null
    )

    @Serializable
    data class TagRcmd(
        val name: String,
        val match: Boolean,
        val id: Long? = null
    )
}

@Serializable
data class ArtistFansResponse(
    val userProfile: UserProfile
) {
    @Serializable
    data class UserProfile(
        val nickname: String,
        val avatarUrl: String,
        val signature: String? = null,
        val followed: Boolean,
        val userId: Long
    )
}

@Serializable
data class EventVO(
    val user: User,
    val eventTime: Long,
    val pics: List<String>,
    val bottomTags: List<EventTag>,
    val eventJson: EventJson,
) {


    @Serializable
    data class EventTag(
        val id: String,
        val name: String
    )

    @Serializable
    data class EventJson(
        val title: String,
//        @SerialName("msg")
        val message: String,
        val song: EventSong? = null,
        val album: EventAlbumRoot? = null,
        val playlist: EventPlaylist? = null,
    )

    @Serializable
    data class EventPlaylist(
        val id: String,
        val name: String,
        val coverImgUrl: String,
        val creator: User
    )

    @Serializable
    data class EventAlbumRoot(
        val artist: EventArtist,
        val name: String,
        val type: String,
        val picUrl: String,
        val id: Long,
    )

    @Serializable
    data class EventSong(
        val name: String,
        val id: Long,
        val artists: List<EventArtist>,
        val album: EventAlbum
    )

    @Serializable
    data class EventArtist(
        val name: String,
        val id: Long,
//        @SerialName("img1v1Url")
        val imageUrl: String,
    )

    @Serializable
    data class EventAlbum(
        val name: String,
        val id: Long,
        val type: String,
        val picUrl: String,
    )
}