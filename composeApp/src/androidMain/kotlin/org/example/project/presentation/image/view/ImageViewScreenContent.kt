package org.example.project.presentation.image.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.project.editor.transformation.draw.DrawTransformation

@Composable
internal fun ImageViewScreenContent(
    state: ImageViewScreen.State,
    onAction: (ImageViewScreen.Action) -> Unit
) {
    val transformation = remember {
        DrawTransformation(state.image.path)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .border(width = 1.dp, color = Color.Red)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        transformation.Content()
        val scope = rememberCoroutineScope { Dispatchers.IO }
        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = {
                scope.launch {
                    val image = transformation.save()
                    onAction(ImageViewScreen.Action.Save(image))
                }
            }
        ) {
            Text("Save")
        }
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.TopCenter)
//                .background(color = MaterialTheme.colorScheme.secondaryContainer)
//                .padding(horizontal = 8.dp, vertical = 8.dp),
//            horizontalArrangement = Arrangement.Start,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            val navigator = LocalNavigator.currentOrThrow
//            IconButton(
//                onClick = {
//                    navigator.pop()
//                },
//                content = {
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                }
//            )
//
//            Text(
//                text = state.image.name,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//                style = MaterialTheme.typography.titleMedium,
//                color = MaterialTheme.colorScheme.primary
//            )
//        }
//        val configuration = LocalConfiguration.current
//        val density = LocalDensity.current
//        val pictureSize = remember(configuration) {
//            val (widthPx, heightPx) = with(density) {
//                val width = configuration.screenWidthDp
//                val height = configuration.screenHeightDp
//                width.dp.roundToPx() to height.dp.roundToPx()
//            }
//            Size(widthPx, heightPx)
//        }
//
//        AsyncImage(
//            modifier = Modifier.fillMaxSize(),
//            model = ImageRequest
//                .Builder(LocalContext.current)
//                .size(pictureSize)
//                .data(state.image.path)
//                .crossfade(true)
//                .build(),
//            contentDescription = null
//        )
//
//        Row(
//            modifier = Modifier
//                .padding(16.dp)
//                .align(Alignment.BottomCenter),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            val onBackgroundColor = MaterialTheme.colorScheme.onBackground
//
//            state.transformations.fastForEach { transformation ->
//                Box(
//                    modifier = Modifier
//                        .clip(CircleShape)
//                        .border(
//                            width = 1.dp,
//                            color = MaterialTheme.colorScheme.onBackground,
//                            shape = CircleShape
//                        )
//                        .padding(8.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Image(
//                        modifier = Modifier.size(32.dp),
//                        painter = painterResource(transformation.iconRes),
//                        contentDescription = null,
//                        colorFilter = remember { ColorFilter.tint(onBackgroundColor) }
//                    )
//                }
//
//                Spacer(Modifier.width(8.dp))
//            }
//        }
    }
}