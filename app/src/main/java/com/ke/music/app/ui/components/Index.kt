package com.ke.music.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun RetryView(retry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        OutlinedButton(onClick = retry) {
            Text("出错了，点我重试")
        }
    }
}

enum class ScreenSize(val maxWidth: Dp) {
    Compact(600.dp),
    Medium(840.dp),
    Expanded(Dp.Infinity)
}

fun Dp.screenSize(): ScreenSize {
    return if (this < ScreenSize.Compact.maxWidth) {
        ScreenSize.Compact
    } else if (this < ScreenSize.Medium.maxWidth) {
        ScreenSize.Medium
    } else {
        ScreenSize.Expanded
    }
}