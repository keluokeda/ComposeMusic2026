package com.ke.music.app.ui.screen.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.AddToPhotos
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ke.music.app.player.RepeatMode
import coil3.compose.AsyncImage
import com.ke.music.app.MusicViewModel
import com.ke.music.app.ui.components.ScreenSize
import com.ke.music.app.ui.components.screenSize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerRoute(
    musicViewModel: MusicViewModel,
    onBack: () -> Unit
) {

    val isPlaying by musicViewModel.isPlaying.collectAsState()
    val duration by musicViewModel.duration.collectAsState()
    val currentMetadata by musicViewModel.currentMetadata.collectAsState()
    val repeatMode by musicViewModel.repeatMode.collectAsState()
    val currentMediaItemIndex by musicViewModel.currentIndex.collectAsState()

    var currentPosition by remember { mutableLongStateOf(musicViewModel.getCurrentPosition()) }
    var showPlaylist by remember { mutableStateOf(false) }
    var showLyrics by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = musicViewModel.getCurrentPosition()
            delay(1000.milliseconds)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("正在播放") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val screenSize = maxWidth.screenSize()

            if (screenSize == ScreenSize.Compact) {
                PlayerCompact(

                    currentMetadata = currentMetadata,
                    currentPosition = currentPosition,
                    duration = duration,
                    isPlaying = isPlaying,
                    repeatMode = repeatMode,
                    showLyrics = showLyrics,
                    onToggleLyrics = { showLyrics = !showLyrics },
                    onShowPlaylist = { showPlaylist = true },
                    onToggleLike = { musicViewModel.toggleLike() },
                    isLiked = musicViewModel.currentSongLiked,
                    lrcString = musicViewModel.currentLrc,
                    seekTo = {
                        musicViewModel.seekTo(it)
                    },
                    toggleRepeatMode = musicViewModel::toggleRepleatMode,
                    skipToPrevious = musicViewModel::skipToPrevious,
                    skipToNext = musicViewModel::skipToNext,
                    pause = musicViewModel::pause,
                    resume = musicViewModel::resume
                )
            } else {
                PlayerMedium(
                    musicViewModel = musicViewModel,
                    currentMetadata = currentMetadata,
                    currentPosition = currentPosition,
                    duration = duration,
                    isPlaying = isPlaying,
                    repeatMode = repeatMode,
                    onShowPlaylist = { showPlaylist = true },
                    onToggleLike = { musicViewModel.toggleLike() },
                    isLiked = musicViewModel.currentSongLiked,
                    seekTo = {
                        musicViewModel.seekTo(it)
                    },
                    toggleRepeatMode = musicViewModel::toggleRepleatMode,
                    skipToPrevious = musicViewModel::skipToPrevious,
                    skipToNext = musicViewModel::skipToNext,
                    pause = musicViewModel::pause,
                    resume = musicViewModel::resume
                )
            }
        }
    }

    if (showPlaylist) {
        val playlistState = rememberLazyListState()
        val songs = musicViewModel.songs

        LaunchedEffect(showPlaylist) {
            if (currentMediaItemIndex >= 0 && currentMediaItemIndex < songs.size) {
                playlistState.scrollToItem(currentMediaItemIndex)
            }
        }

        ModalBottomSheet(
            onDismissRequest = { showPlaylist = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
            ) {
                Text(
                    "当前播放列表 (${songs.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()
                LazyColumn(
                    state = playlistState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(songs) { index, song ->
                        val isCurrent = index == currentMediaItemIndex
                        ListItem(
                            headlineContent = {
                                Text(
                                    song.name,
                                    color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            supportingContent = {
                                Text(
                                    song.artists.joinToString("/") { it.name },
                                    color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.clickable {
                                musicViewModel.playAtIndex(index)
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showPlaylist = false
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerCompact(

    currentMetadata: com.ke.music.app.player.SongMetadata?,
    currentPosition: Long,
    duration: Long,
    isPlaying: Boolean,
    repeatMode: RepeatMode,
    showLyrics: Boolean,
    onToggleLyrics: () -> Unit,
    onShowPlaylist: () -> Unit,
    onToggleLike: () -> Unit,
    isLiked: Boolean,
    lrcString: String? = null,
    seekTo: (Long) -> Unit = {},

    toggleRepeatMode: () -> Unit,
    skipToPrevious: () -> Unit,
    pause: () -> Unit,
    resume: () -> Unit,
    skipToNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable { onToggleLyrics() },
            contentAlignment = Alignment.Center
        ) {
            if (showLyrics) {
                LyricsView(lrcString, currentPosition)
            } else {
                AnimatedContent(
                    targetState = currentMetadata?.artworkUrl,
                    transitionSpec = { fadeIn().togetherWith(fadeOut()) },
                    label = "AlbumArtTransition"
                ) { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(MaterialTheme.shapes.large),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(onClick = {}) { Icon(Icons.Default.Share, null) }
            IconButton(onClick = {}) { Icon(Icons.Default.AddToPhotos, null) }
            IconButton(onClick = onToggleLike) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(onClick = {}) { Icon(Icons.AutoMirrored.Filled.Comment, null) }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Slider(
            value = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f,
            onValueChange = { seekTo((it * duration).toLong()) },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = formatTime(currentPosition))
            Text(text = formatTime(duration))
        }

        Spacer(modifier = Modifier.height(32.dp))

        PlayerControls(
            isPlaying = isPlaying,
            repeatMode = repeatMode,
            onShowPlaylist = onShowPlaylist,
            toggleRepeatMode = toggleRepeatMode,
            skipToNext = skipToNext,
            resume = resume,
            pause = pause,
            skipToPrevious = skipToPrevious
        )
    }
}

@Composable
private fun PlayerMedium(
    musicViewModel: MusicViewModel,
    currentMetadata: com.ke.music.app.player.SongMetadata?,
    currentPosition: Long,
    duration: Long,
    isPlaying: Boolean,
    repeatMode: RepeatMode,
    onShowPlaylist: () -> Unit,
    onToggleLike: () -> Unit,
    isLiked: Boolean,
    seekTo: (Long) -> Unit,

    toggleRepeatMode: () -> Unit,
    skipToPrevious: () -> Unit,
    pause: () -> Unit,
    resume: () -> Unit,
    skipToNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedContent(
                targetState = currentMetadata?.artworkUrl,
                transitionSpec = { fadeIn().togetherWith(fadeOut()) },
                label = "AlbumArtTransition"
            ) { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.large),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(onClick = {}) { Icon(Icons.Default.Share, null) }
                IconButton(onClick = {}) { Icon(Icons.Default.AddToPhotos, null) }
                IconButton(onClick = onToggleLike) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(onClick = {}) { Icon(Icons.AutoMirrored.Filled.Comment, null) }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Slider(
                value = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f,
                onValueChange = { seekTo((it * duration).toLong()) },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = formatTime(currentPosition))
                Text(text = formatTime(duration))
            }

            Spacer(modifier = Modifier.height(32.dp))

            PlayerControls(
                isPlaying = isPlaying,
                repeatMode = repeatMode,
                onShowPlaylist = onShowPlaylist,
                toggleRepeatMode = toggleRepeatMode,
                skipToNext = skipToNext,
                resume = resume,
                pause = pause,
                skipToPrevious = skipToPrevious
            )
        }

        Spacer(modifier = Modifier.width(32.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            LyricsView(musicViewModel.currentLrc, currentPosition)
        }
    }
}

@Composable
private fun PlayerControls(

    isPlaying: Boolean,
    repeatMode: RepeatMode,
    onShowPlaylist: () -> Unit,


    toggleRepeatMode: () -> Unit,
    skipToPrevious: () -> Unit,
    pause: () -> Unit,
    resume: () -> Unit,
    skipToNext: () -> Unit

) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = toggleRepeatMode) {
            val icon = when (repeatMode) {
                RepeatMode.SHUFFLE -> Icons.Default.Shuffle
                RepeatMode.ONE -> Icons.Default.RepeatOne
                RepeatMode.LIST -> Icons.Default.Repeat
            }
            Icon(icon, null, modifier = Modifier.size(32.dp))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(onClick = { skipToPrevious() }) {
                Icon(Icons.Default.SkipPrevious, null, modifier = Modifier.size(48.dp))
            }

            IconButton(
                onClick = { if (isPlaying) pause() else resume() },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    null,
                    modifier = Modifier.size(64.dp)
                )
            }

            IconButton(onClick = { skipToNext() }) {
                Icon(Icons.Default.SkipNext, null, modifier = Modifier.size(48.dp))
            }
        }

        IconButton(onClick = onShowPlaylist) {
            Icon(Icons.AutoMirrored.Filled.PlaylistPlay, null, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
private fun LyricsView(lrc: String?, currentPosition: Long) {
    val lrcLines = remember(lrc) { LrcParser.parse(lrc) }
    val listState = rememberLazyListState()

    val currentIndex by remember(lrcLines, currentPosition) {
        derivedStateOf {
            lrcLines.indexOfLast { it.time <= currentPosition }.coerceAtLeast(0)
        }
    }

    LaunchedEffect(currentIndex) {
        if (lrcLines.isNotEmpty()) {
            listState.animateScrollToItem(currentIndex, scrollOffset = -200)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (lrcLines.isEmpty()) {
            Text(
                text = "暂无歌词",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(lrcLines) { index, line ->
                    val isCurrent = index == currentIndex
                    Text(
                        text = line.content,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = if (isCurrent) 20.sp else 16.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.6f
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
