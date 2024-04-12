package org.example.project.presentation.image.list

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.util.fastForEach
import cafe.adriel.voyager.koin.getScreenModel
import implementation.domain.models.Image
import org.example.project.presentation.base.BaseScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.round
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch

internal class ImageListScreen :
    BaseScreen<ImageListScreen.Action, ImageListScreen.State, ImageListScreenModel>() {

    @Immutable
    data class State(val imagesList: List<Image>)

    sealed interface Action

    @Composable
    override fun Content(state: State, onAction: (Action) -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            state.imagesList.fastForEach {
                var startPosition by remember {
                    mutableStateOf(Offset.Unspecified)
                }
                var verticalOffset by remember {
                    mutableFloatStateOf(0f)
                }
                var horizontalOffset by remember {
                    mutableFloatStateOf(0f)
                }
                var dragEnabled by remember {
                    mutableStateOf(true)
                }
                var isSelected by remember {
                    mutableStateOf(false)
                }
                val scope = rememberCoroutineScope()

                val moveToStart: () -> Unit = {
                    scope.launch {
                        dragEnabled = false
                        val spec = spring<Float>(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )

                        val verticalJob = launch {
                            Animatable(initialValue = verticalOffset)
                                .animateTo(
                                    targetValue = 0f,
                                    animationSpec = spec,
                                    block = {
                                        verticalOffset = value
                                    }
                                )
                        }

                        val horizontalJob = launch {
                            Animatable(initialValue = horizontalOffset)
                                .animateTo(
                                    targetValue = 0f,
                                    animationSpec = spec,
                                    block = {
                                        horizontalOffset = value
                                    }
                                )
                        }

                        verticalJob.join()
                        horizontalJob.join()

                        dragEnabled = true
                        isSelected = false
                    }
                }

                Box(
                    modifier = Modifier
                        .let {
                            if (isSelected) {
                                it.zIndex(2f)
                            } else {
                                it
                            }
                        }
                        .onGloballyPositioned {
                            val position = it.positionInRoot()
                            startPosition = position
                        }
                        .offset {
                            if (startPosition.isValid()) {
                                val intPosition = startPosition.round()

                                intPosition.copy(
                                    x = intPosition.x + horizontalOffset.fastRoundToInt(),
                                    y = intPosition.y + verticalOffset.fastRoundToInt()
                                )
                            } else {
                                IntOffset.Zero
                            }
                        }
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = {
                                    isSelected = true
                                },
                                onDragEnd = moveToStart,
                                onDragCancel = moveToStart,
                                onDrag = { pointerInputChange, offset ->
                                    if (dragEnabled) {
                                        pointerInputChange.consume()
                                        verticalOffset += offset.y
                                        horizontalOffset += offset.x
                                    }
                                }
                            )
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colors.secondary, CircleShape),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(it.id)
                        Text(it.name)
                        Text(it.path)
                        Text(it.saveDate.toString())
                    }
                }
            }
        }
    }

    @Composable
    override fun getModel(): ImageListScreenModel {
        return getScreenModel<ImageListScreenModel>()
    }
}