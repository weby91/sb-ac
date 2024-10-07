package jp.speakbuddy.edisonandroidexercise.presentation.commons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * ErrorContent is a composable function that displays an error message
 * in a visually appealing way. It uses an animated visibility effect to
 * show the error message for a brief period (3 seconds) before fading it out.
 * 
 * The error message is displayed inside a red box with an error icon,
 * providing a clear indication to the user that something went wrong.
 * 
 * @param errorMessage The message to be displayed when an error occurs.
 */

@Composable
fun ErrorContent(errorMessage: String) {
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = errorMessage) {
        isVisible = true
        delay(3000)
        isVisible = false
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Red.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = errorMessage,
                    color = Color.Black // Changed text color to black for better contrast
                )
            }
        }
    }
}