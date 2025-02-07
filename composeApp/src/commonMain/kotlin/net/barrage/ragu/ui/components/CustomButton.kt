package net.barrage.ragu.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

@Composable
fun CustomButton(
    onClick: () -> Unit,
    shape: Shape = ButtonDefaults.shape,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClick, shape = shape, modifier = modifier, colors = ButtonDefaults.buttonColors(

        )
    ) {
        content()
    }
}