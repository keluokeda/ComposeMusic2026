package com.ke.music.app.data.model

import com.ke.music.app.ui.screen.playlist.PlaylistDetailViewModel

object Mock {
    val avatar = "https://p1.music.126.net/z76r1ryNn4W347KUgDjePg==/109951169814030568.jpg"


    val user = User(1L, "Nickname", avatar, false, null)
    val playlist = Playlist(
        id = 1L,
        creator = user,
        coverImgUrl = avatar,
        name = "Playlist Name",
        tags = listOf("Tag1", "Tag2"),
        description = "Description",
        trackCount = 10,
        playCount = 100,
        subscribers = emptyList(),
        updateTime = 0L
    )
    val songs = listOf(
        Song(
            1L,
            "Song 1",
            Album(1L, "Album 1", avatar),
            listOf(Artist(1L, "Artist 1", avatar)),
            0L
        ),
        Song(2L, "Song 2", Album(2L, "Album 2", avatar), listOf(Artist(2L, "Artist 2", avatar)), 0L)
    )
    val dynamic = PlaylistDynamicResponse(10, 100, 5, false, 0)

    val comment =
        CommentVO(0L, user, "你好，很高兴认识你", "2025-12-12", 0L, 998, "北京", false, true, 1L, 99)
}