package com.ke.music.app.ui.screen.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ke.music.app.ui.components.LoadingView
import com.ke.music.app.ui.components.RetryView
import com.ke.music.app.ui.components.ScreenSize
import com.ke.music.app.ui.components.screenSize
import com.ke.music.app.ui.theme.MusicTheme
import com.lightspark.composeqr.QrCodeView
import com.orhanobut.logger.Logger

@Composable
fun LoginRoute(
    onLoginSuccess: () -> Unit,
) {
    val viewModel: LoginViewModel = hiltViewModel()

    val uiState = viewModel.uiState

    LaunchedEffect(uiState) {
        if ((uiState as? LoginViewModel.UiState.Success)?.success == true) {
            onLoginSuccess()
        }
    }


    LoginScreen(uiState, refresh = viewModel::createKey, viewModel::checkLogin)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginScreen(
    uiState: LoginViewModel.UiState,
    refresh: () -> Unit = {},
    login: (String) -> Unit = {}
) {

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text("扫码登录")
            }
        )
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                LoginViewModel.UiState.Error -> RetryView(refresh)
                LoginViewModel.UiState.Loading -> LoadingView()
                is LoginViewModel.UiState.Success -> {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        Logger.d(this.maxWidth)
                        if (maxWidth.screenSize() == ScreenSize.Compact) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.7f)
                                        .aspectRatio(1f), contentAlignment = Alignment.Center
                                ) {

                                    if (uiState.loading) {
                                        CircularProgressIndicator()
                                    } else {
                                        QrCodeView(
                                            data = uiState.url,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

                                OutlinedButton(
                                    onClick = refresh,
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !uiState.loading
                                ) {
                                    Text("刷新二维码")
                                }

                                Button(
                                    onClick = {
                                        login(uiState.url)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !uiState.loading
                                ) {
                                    Text("我已扫码")
                                }


                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.7f)
                                            .aspectRatio(1f), contentAlignment = Alignment.Center
                                    ) {

                                        if (uiState.loading) {
                                            CircularProgressIndicator()
                                        } else {
                                            QrCodeView(
                                                data = uiState.url,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(32.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = refresh,
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = !uiState.loading
                                    ) {
                                        Text("刷新二维码")
                                    }

                                    Button(
                                        onClick = {
                                            login(uiState.url)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = !uiState.loading
                                    ) {
                                        Text("我已扫码")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
fun LoginScreenSuccessIdlePreview() {
    MusicTheme {
        LoginScreen(uiState = LoginViewModel.UiState.Success(url = "https://github.com/lightsparkdev/compose-qr-code"))
    }
}
