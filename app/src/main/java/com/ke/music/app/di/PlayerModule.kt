package com.ke.music.app.di

import android.content.ComponentName
import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.ke.music.app.player.IPlayer
import com.ke.music.app.player.MusicPlayer
import com.ke.music.app.player.PlaybackService
import com.google.common.util.concurrent.ListenableFuture
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {

    @Provides
    @Singleton
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer {
        return ExoPlayer.Builder(context).build()
    }

    @Provides
    @Singleton
    fun provideMediaControllerFuture(@ApplicationContext context: Context): ListenableFuture<MediaController> {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        return MediaController.Builder(context, sessionToken).buildAsync()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerBindingModule {
    @Binds
    @Singleton
    abstract fun bindIPlayer(impl: MusicPlayer): IPlayer
}
