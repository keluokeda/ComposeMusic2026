package com.ke.music.app.ui.screen.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.ke.music.app.data.model.CommentVO
import com.ke.music.app.data.model.Mock
import com.ke.music.app.format
import com.ke.music.app.ui.components.ScreenSize
import com.ke.music.app.ui.components.screenSize
import com.ke.music.app.ui.navigation.Destination
import com.ke.music.app.ui.theme.MusicTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsRoute(
    viewModel: CommentsViewModel,
    onBack: () -> Unit,
    navigate: (Destination) -> Unit
) {
    val items = viewModel.comments.collectAsLazyPagingItems()

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text("评论")
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }
            }
        )
    }) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val screenSize = maxWidth.screenSize()

            if (screenSize == ScreenSize.Compact) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(items.itemCount, key = items.itemKey { it.commentId }) { index ->
                        val comment = items[index]!!


                        Column(
                            Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CommentCard(comment = comment)

                            HorizontalDivider()
                        }
                    }
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(300.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalItemSpacing = 16.dp,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(items.itemCount, key = items.itemKey { it.commentId }) { index ->
                        val comment = items[index]!!


                        OutlinedCard {
                            CommentCard(modifier = Modifier.padding(8.dp), comment = comment)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentCard(
    modifier: Modifier = Modifier,
    comment: CommentVO,
    navigate: (Destination) -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        AsyncImage(
            model = comment.user.avatarUrl,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(comment.user.nickname, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        comment.timeString + " " + comment.ipLocation,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                TextButton(onClick = {}) {
                    Text(comment.likedCount.format())
                    Icon(
                        if (comment.liked) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp,
                        null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(comment.content ?: "")
        }
    }
}

@Composable
@PreviewLightDark
private fun CommentCardPreview() {
    MusicTheme {
        Scaffold(

        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(16.dp)
            ) {
                CommentCard(comment = Mock.comment)
            }
        }
    }
}