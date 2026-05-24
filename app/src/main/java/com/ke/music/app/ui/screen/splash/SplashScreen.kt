package com.ke.music.app.ui.screen.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ke.music.app.ui.components.LoadingView
import com.ke.music.app.ui.components.RetryView
import com.ke.music.app.ui.navigation.Destination

@Composable
fun SplashRoute(
    next: (Destination) -> Unit
) {
    val viewModel = hiltViewModel<SplashViewModel>()

    val uiState = viewModel.uiState

    LaunchedEffect(uiState) {
        if (uiState.userId != null) {
            next(if (uiState.userId == 0L) Destination.Login else Destination.Main(uiState.userId))
        }
    }

    Scaffold { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            if (uiState.error) {
                RetryView {
                    viewModel.checkLogin()
                }
            } else {
                LoadingView()
            }
        }
    }
}