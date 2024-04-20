package org.example.project.presentation.image.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

@Composable
internal fun ImageViewScreenContent(
    state: ImageViewScreen.State,
    onAction: (ImageViewScreen.Action) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        state.imageEditor.Content()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(color = Color.Black.copy(alpha = 0.5f))
                .padding(horizontal = 8.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val navigator = LocalNavigator.currentOrThrow
            IconButton(
                onClick = {
                    navigator.pop()
                },
                content = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            )

            Text(
                text = state.image.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .imePadding()
                .padding(
                    start = 24.dp,
                    bottom = 24.dp
                ),
            onClick = {
                onAction(ImageViewScreen.Action.Save)
            }
        ) {
            Text("Save as file")
        }
    }
}