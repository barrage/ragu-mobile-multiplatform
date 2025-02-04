package net.barrage.ragu.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.theolm.rinku.DeepLink
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.barrage.ragu.ui.components.AppIconCard
import net.barrage.ragu.ui.components.ErrorDialog
import net.barrage.ragu.ui.components.ErrorDialogState
import net.barrage.ragu.utils.DeepLinkParser
import net.barrage.ragu.utils.fixCenterTextOnAllPlatforms
import net.barrage.ragu.utils.isDebug
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ragumultiplatform.composeapp.generated.resources.Res
import ragumultiplatform.composeapp.generated.resources.choose_a_login_method
import ragumultiplatform.composeapp.generated.resources.continue_with_google
import ragumultiplatform.composeapp.generated.resources.ic_google
import ragumultiplatform.composeapp.generated.resources.login
import ragumultiplatform.composeapp.generated.resources.login_error_retry_button_text
import ragumultiplatform.composeapp.generated.resources.login_error_title
import ragumultiplatform.composeapp.generated.resources.login_failed

@Composable
fun LoginScreen(
    navigateToChat: () -> Unit,
    onGoogleLogin: (String) -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
    deepLink: DeepLink?,
    scope: CoroutineScope,
    modifier: Modifier = Modifier,
) {
    val loginState by viewModel.loginState.collectAsState()

    val rememberedOnGoogleLogin by rememberUpdatedState(onGoogleLogin)
    val rememberedNavigateToChat by rememberUpdatedState(navigateToChat)

    DisposableEffect(deepLink) {
        val job = scope.launch {
            snapshotFlow { deepLink }
                .collect { currentDeepLink ->
                    if (currentDeepLink != null && loginState is LoginScreenState.Idle) {
                        val code = DeepLinkParser.extractCodeFromDeepLink(currentDeepLink.data)
                        if (code != null) {
                            viewModel.login(code)
                        }
                    }
                }
        }

        onDispose {
            job.cancel()
        }
    }

    Box(modifier = modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
        when (loginState) {
            is LoginScreenState.Idle -> {
                LoginContent(
                    onGoogleLogin = {
                        scope.launch {
                            val codeVerifier = viewModel.generateCodeVerifier()
                            rememberedOnGoogleLogin(codeVerifier)
                        }
                    }
                )
            }

            is LoginScreenState.Loading -> {
                CircularProgressIndicator()
            }

            is LoginScreenState.Success -> {
                LaunchedEffect(Unit) { rememberedNavigateToChat() }
            }

            is LoginScreenState.Error -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    ErrorDialog(
                        state =
                        ErrorDialogState(
                            title = stringResource(Res.string.login_error_title),
                            description =
                            "${stringResource(Res.string.login_failed)}${
                                if (isDebug)
                                    ": ${
                                        if ((loginState as LoginScreenState.Error).message != null) (loginState as LoginScreenState.Error).message else stringResource(
                                            (loginState as LoginScreenState.Error).messageRes
                                        )
                                    }"
                                else ""
                            }",
                            onDismissRequest = {},
                            confirmButton = {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            val codeVerifier = viewModel.generateCodeVerifier()
                                            rememberedOnGoogleLogin(codeVerifier)
                                        }
                                    }
                                ) {
                                    Text(
                                        stringResource(Res.string.login_error_retry_button_text)
                                    )
                                }
                            },
                        ),
                        modifier = Modifier.padding(20.dp).align(Alignment.Center),
                    )
                }
            }
        }
    }
}

@Composable
fun LoginContent(onGoogleLogin: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        colors =
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(16.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp),
        ) {
            AppIconCard()
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                stringResource(Res.string.login),
                style = MaterialTheme.typography.headlineMedium.fixCenterTextOnAllPlatforms(),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                stringResource(Res.string.choose_a_login_method),
                style = MaterialTheme.typography.bodyMedium.fixCenterTextOnAllPlatforms(),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onGoogleLogin,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_google),
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        stringResource(Res.string.continue_with_google),
                        style = MaterialTheme.typography.bodyMedium.fixCenterTextOnAllPlatforms(),
                    )
                }
            }
        }
    }
}
