package org.example.project.presentation.image.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.example.project.editor.transformation.Transformation

private val floatSpec = spring<Float>(
    stiffness = Spring.StiffnessLow,
)
private val intOffsetSpec = spring(
    stiffness = Spring.StiffnessLow,
    visibilityThreshold = IntOffset.VisibilityThreshold
)

@Composable
internal fun ImageViewScreenContent(
    state: ImageViewScreen.State,
    onAction: (ImageViewScreen.Action) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppBar(
            modifier = Modifier.fillMaxWidth(),
            imageName = state.image.name,
            trailingButton = {
                val alpha by animateFloatAsState(
                    targetValue = if (state.editingState !is EditingState.TransformationSelected) {
                        1f
                    } else {
                        0f
                    },
                    label = ""
                )

                IconButton(
                    modifier = Modifier.alpha(alpha),
                    content = {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        when (state.editingState) {
                            EditingState.Initial -> Unit

                            is EditingState.SavedBitmap ->
                                onAction(
                                    ImageViewScreen.Action.Save(
                                        state.editingState.bitmap.asAndroidBitmap()
                                    )
                                )

                            is EditingState.TransformationSelected ->
                                onAction(
                                    ImageViewScreen.Action.Save(
                                        state.editingState.transformation.save()
                                    )
                                )
                        }
                    }
                )
            }
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .animateContentSize()
        ) {
            when (val editingState = state.editingState) {
                EditingState.Initial ->
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = ImageRequest
                            .Builder(LocalContext.current)
                            .data(state.image.path)
                            .build(),
                        contentDescription = null
                    )

                is EditingState.SavedBitmap ->
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        bitmap = editingState.bitmap,
                        contentDescription = null
                    )

                is EditingState.TransformationSelected ->
                    editingState.transformation.Content()
            }
        }

        AnimatedContent(
            targetState = state.editingState,
            label = "",
            transitionSpec = {
                slideIntoContainer(
                    animationSpec = intOffsetSpec,
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                ) + fadeIn(floatSpec) togetherWith slideOutOfContainer(
                    animationSpec = intOffsetSpec,
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                ) + fadeOut(floatSpec)
            }
        ) { editingState ->
            if (editingState is EditingState.TransformationSelected) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    editingState.transformation.Controls()
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {

                                }
                                .rotate(135f),
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null
                        )
                        Icon(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    onAction(
                                        ImageViewScreen.Action.UpdateBitmap(
                                            bitmap = editingState.transformation.save()
                                        )
                                    )
                                },
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null
                        )
                    }
                }
            } else {
                BottomBar(
                    modifier = Modifier.fillMaxWidth(),
                    transformations = state.transformations,
                    onTransformationClick = {
                        onAction(
                            ImageViewScreen.Action.SelectTransformation(
                                it
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun AppBar(
    modifier: Modifier,
    imageName: String,
    trailingButton: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .padding(8.dp),
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
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )

        Text(
            modifier = Modifier.weight(1f),
            text = imageName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        trailingButton()
    }
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    transformations: List<Transformation>,
    onTransformationClick: (Transformation) -> Unit,
) {
    Row(
        modifier = modifier.padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val onBackgroundColor = MaterialTheme.colorScheme.onBackground

        transformations.fastForEach { transformation ->
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onTransformationClick(transformation) }
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = CircleShape
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(transformation.iconRes),
                    contentDescription = null,
                    colorFilter = remember { ColorFilter.tint(onBackgroundColor) }
                )
            }

            Spacer(Modifier.width(8.dp))
        }
    }
}