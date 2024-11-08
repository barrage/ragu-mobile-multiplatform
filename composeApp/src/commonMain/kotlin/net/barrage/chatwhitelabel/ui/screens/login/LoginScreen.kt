package net.barrage.chatwhitelabel.ui.screens.login

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chatwhitelabel.composeapp.generated.resources.Res
import chatwhitelabel.composeapp.generated.resources.ic_google
import dev.theolm.rinku.DeepLink
import net.barrage.chatwhitelabel.ui.components.AppIconCard
import net.barrage.chatwhitelabel.utils.DeepLinkParser
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onGoogleLogin: () -> Unit,
    navigateToChat: () -> Unit,
    deepLink: DeepLink?,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val loginState = viewModel.loginState

    val currentNavigateToChat by rememberUpdatedState(navigateToChat)

    LaunchedEffect(deepLink) {
        if (deepLink != null && viewModel.loginState is LoginScreenState.Idle) {
            val code = DeepLinkParser.extractCodeFromDeepLink(deepLink.data)
            if (code != null) {
                viewModel.login(code)
            }
        }
    }

    LaunchedEffect(loginState) {
        if (loginState is LoginScreenState.Success) {
            currentNavigateToChat()
        }
    }

    Box(modifier = modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
        when (loginState) {
            is LoginScreenState.Idle -> {
                LoginContent(onGoogleLogin)
            }

            is LoginScreenState.Loading -> {
                CircularProgressIndicator()
            }

            is LoginScreenState.Success -> {
                // Navigation is handled in LaunchedEffect
            }

            is LoginScreenState.Error -> {
                Text("Error: ${loginState.message}")
            }
        }
    }
}

@Composable
fun LoginContent(onGoogleLogin: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        colors =
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp),
        ) {
            AppIconCard()
            Spacer(modifier = Modifier.height(20.dp))
            Text("Login", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(20.dp))
            Text("Choose a login method", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onGoogleLogin,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
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
                    Text("Continue with Google", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
