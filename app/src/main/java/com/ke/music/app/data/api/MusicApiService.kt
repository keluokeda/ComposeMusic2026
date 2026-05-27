package com.ke.music.app.data.api

import com.ke.music.app.data.model.*
import retrofit2.http.*

interface MusicApiService {

    @GET("key")
    suspend fun createLoginKey(): BaseVO<CreateLoginKeyVO>

    @GET("login")
    suspend fun login(@Query("key") key: String): BaseVO<LoginVO>

    @GET("login/status")
    suspend fun loginStatus(): BaseVO<Long>

    @GET("user/{id}")
    suspend fun userDetail(@Path("id") id: Long): BaseVO<UserDetailVO>

    @GET("user/current/playlists")
    suspend fun currentUserPlaylists(): BaseVO<List<Playlist>>

    @GET("user/{userId}/playlists")
    suspend fun userPlaylists(@Path("userId") userId: Long): BaseVO<List<Playlist>>

    @GET("user/{id}/follows")
    suspend fun userFollows(
        @Path("id") id: Long,
        @Query("index") index: Int,
        @Query("pageSize") pageSize: Int
    ): BaseListVO<User>

    @GET("user/{id}/followeds")
    suspend fun userFolloweds(
        @Path("id") id: Long,
        @Query("index") index: Int,
        @Query("pageSize") pageSize: Int
    ): BaseListVO<User>

    @GET("playlist/{id}")
    suspend fun playlistDetail(@Path("id") id: Long): BaseVO<PlaylistDetailVO>

    @DELETE("playlist/{id}")
    suspend fun deletePlaylist(@Path("id") id: Long): BaseVO<Unit>

    @GET("playlist/{id}/subscribers")
    suspend fun playlistSubscribers(
        @Path("id") id: Long,
        @Query("index") index: Int,
        @Query("size") size: Int
    ): BaseListVO<User>

    @POST("playlist/{id}/subscribers")
    suspend fun createSubscription(@Path("id") id: Long): BaseVO<Unit>

    @DELETE("playlist/{id}/subscribers")
    suspend fun deleteSubscription(@Path("id") id: Long): BaseVO<Unit>

    @PATCH("user/current/playlist/{playlistId}/songs")
    suspend fun addToPlaylist(
        @Path("playlistId") playlistId: Long,
        @Query("songIds") songIds: List<Long>
    ): BaseVO<Unit>

    @DELETE("user/current/playlist/{playlistId}/songs/{songId}")
    suspend fun deleteSongFromPlaylist(
        @Path("playlistId") playlistId: Long,
        @Path("songId") songId: Long
    ): BaseVO<Unit>

    @GET("song/{id}")
    suspend fun songDetail(@Path("id") id: Long): BaseVO<SongDetailVO>

    @GET("artist/{id}")
    suspend fun getArtistDetail(@Path("id") id: Long): BaseVO<ArtistDetailVO>

    @GET("album/{id}")
    suspend fun albumDetail(@Path("id") id: Long): BaseVO<AlbumDetailVO>

    @GET("mv/{id}")
    suspend fun mvDetail(@Path("id") id: Long): BaseVO<MvDetailVO>

    @GET("comment")
    suspend fun getComments(
        @Query("type") type: Int,
        @Query("id") id: Long,
        @Query("index") index: Int,
        @Query("pageSize") pageSize: Int,
    ): BaseListVO<CommentVO>

    @GET("comment/{commentId}/child")
    suspend fun getChildComments(
        @Path("commentId") commentId: Long,
        @Query("type") type: Int,
        @Query("id") id: Long,
        @Query("time") time: Long,
        @Query("pageSize") pageSize: Int
    ): BaseListVO<CommentVO>

    @GET("message")
    suspend fun getAllMessages(): BaseVO<List<PrivateMessageVO>>

    @GET("message/{userId}")
    suspend fun conversions(
        @Path("userId") userId: Long,
        @Query("before") before: Long
    ): Map<String, String> // The schema for this was additionalProperties: {}

    /**
     * 通知-评论
     */
    @GET("notification/comments")
    suspend fun notificationComments(@Query("before") before: Long?): BaseListVO<NotificationComment>

    /**
     * 通知-通知
     */
    @GET("notification/notices")
    suspend fun notices(@Query("cursor") cursor: Long? = null): BaseListVO<NotificationNoticeResponse>
}
