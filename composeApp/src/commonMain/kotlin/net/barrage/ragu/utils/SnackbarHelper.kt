package net.barrage.ragu.utils

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

class SnackbarHelper(
    private val snackbarHostState: SnackbarHostState,
    private val coroutineScope: CoroutineScope,
    private val getString: (StringResource) -> String
) {

    fun showSnackbar(
        messageRes: StringResource,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration = SnackbarDuration.Short,
        onAction: (() -> Unit)? = null,
    ) {
        coroutineScope.launch {
            val result = snackbarHostState.showSnackbar(
                message = getString(messageRes),
                actionLabel = actionLabel,
                withDismissAction = withDismissAction,
                duration = duration
            )

            when (result) {
                SnackbarResult.ActionPerformed -> onAction?.invoke()
                SnackbarResult.Dismissed -> Unit
            }
        }
    }

    companion object {
        private var instance: SnackbarHelper? = null

        fun initialize(
            snackbarHostState: SnackbarHostState,
            coroutineScope: CoroutineScope,
            getString: (StringResource) -> String
        ) {
            instance = SnackbarHelper(snackbarHostState, coroutineScope, getString)
        }

        fun getInstance(): SnackbarHelper {
            return instance ?: throw IllegalStateException(
                "SnackbarHelper not initialized. Call initialize() first."
            )
        }
    }
}