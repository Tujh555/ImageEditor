package org.example.project.editor.transformation.draw

import android.graphics.Bitmap
import android.graphics.Picture
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import org.example.project.editor.AndroidCanvas
import org.example.project.editor.modifiers.gestures.MotionEvent
import org.example.project.editor.modifiers.gestures.onOneTouchEvents
import org.example.project.editor.transformation.Transformation

class DrawTransformation(private val startBitmap: ImageBitmap) : Transformation {
    private var previousPosition by mutableStateOf(Offset.Unspecified)
    private var drawRect by mutableStateOf(Rect.Zero)
    private var motionEvent by mutableStateOf<MotionEvent>(MotionEvent.Unspecified)
    private val colors = initialColors
    private var pictureCanvas by mutableStateOf<AndroidCanvas?>(null)
    private val paths = mutableStateListOf(
        Path() to Paint().apply {
            color = initialColors.first()
            strokeWidth = 10f
            style = PaintingStyle.Stroke
            isAntiAlias = true
        }
    )
    private var dialogVisible by mutableStateOf(false)
    private var customColor by mutableStateOf(Color.Unspecified)
    private val picture by mutableStateOf(Picture())
    private var isCustomColorSelected by mutableStateOf(false)

    override fun clear() {
        paths.clear()
        paths.add(
            Path() to Paint().apply {
                color = initialColors.first()
                strokeWidth = 10f
                style = PaintingStyle.Stroke
                isAntiAlias = true
            }
        )
    }

    override fun undo() {
        if (paths.size == 1) {
            val paint = paths.last().second
            paths.replaceAll {
                Path() to paint
            }
            return
        }

        paths.removeLast()
    }

    override fun save(): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Bitmap.createBitmap(picture)
        } else {
            val bitmap = Bitmap.createBitmap(picture.width, picture.height, Bitmap.Config.ARGB_8888)
            val canvas = AndroidCanvas(bitmap)
            canvas.drawColor(android.graphics.Color.WHITE)
            canvas.drawPicture(picture)
            bitmap
        }
    }

    @Composable
    override fun Content() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ColorPicker()
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .onOneTouchEvents(key = drawRect) { event ->
                        if (drawRect.contains(event.position)) {
                            motionEvent = event
                        }
                    }
                    .drawWithContent {
                        val width = this.size.width.toInt()
                        val height = this.size.height.toInt()

                        val canvas = picture.beginRecording(width, height)
                        val drawCanvas = Canvas(canvas)

                        pictureCanvas = canvas

                        draw(this, this.layoutDirection, drawCanvas, this.size) {
                            this@drawWithContent.drawContent()
                        }

                        val lastPath = paths.lastOrNull()?.first

                        when (motionEvent) {
                            is MotionEvent.Down -> lastPath?.moveTo(motionEvent.x, motionEvent.y)

                            is MotionEvent.Move ->
                                if (previousPosition.isUnspecified.not()) {
                                    lastPath?.quadraticTo(
                                        x1 = previousPosition.x,
                                        y1 = previousPosition.y,
                                        x2 = (previousPosition.x + motionEvent.x) / 2,
                                        y2 = (previousPosition.y + motionEvent.y) / 2
                                    )
                                }

                            is MotionEvent.Up -> {
                                motionEvent = MotionEvent.Unspecified
                            }

                            MotionEvent.Unspecified -> Unit
                        }

                        previousPosition = motionEvent.position

                        paths.fastForEach { (drawPath, pathPaint) ->
                            drawCanvas.drawPath(
                                path = drawPath,
                                paint = pathPaint
                            )
                        }

                        picture.endRecording()

                        drawIntoCanvas { canvas ->
                            canvas.nativeCanvas.drawPicture(picture)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier
                        .onGloballyPositioned { layoutCoordinates ->
                            val bounds = layoutCoordinates.boundsInParent()
                            if (drawRect != bounds) {
                                drawRect = bounds
                            }
                        },
                    bitmap = startBitmap,
                    contentDescription = null
                )
            }
        }
    }

    @Composable
    private fun ColorPicker() {
        val controller = rememberColorPickerController()

        AnimatedVisibility(
            visible = dialogVisible
        ) {
            Dialog(
                onDismissRequest = {
                    dialogVisible = false
                },
                content = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        var selectedColor by remember(customColor) {
                            mutableStateOf(
                                if (customColor.isUnspecified) {
                                    Color.Red
                                } else {
                                    customColor
                                }
                            )
                        }

                        val screenHeight = LocalConfiguration.current.screenHeightDp
                        val screenWidth = LocalConfiguration.current.screenWidthDp

                        val colorPickerHeight = screenHeight * 0.5f
                        val colorPickerWidth = screenWidth * 0.8f

                        HsvColorPicker(
                            modifier = Modifier
                                .size(
                                    width = colorPickerWidth.dp,
                                    height = colorPickerHeight.dp
                                ),
                            controller = controller,
                            initialColor = if (customColor.isUnspecified) {
                                Color.Red
                            } else {
                                customColor
                            },
                            onColorChanged = { colorEnvelope ->
                                if (colorEnvelope.fromUser) {
                                    selectedColor = colorEnvelope.color
                                }
                            },
                        )
                        AnimatedVisibility(
                            visible = selectedColor != Color.Unspecified
                        ) {
                            Button(
                                content = {
                                    Text(
                                        text = "Выбрать",
                                        color = selectedColor.getContrast().value
                                    )
                                },
                                onClick = {
                                    customColor = selectedColor
                                    isCustomColorSelected = true
                                    applyColor(customColor)
                                    dialogVisible = false
                                },
                                colors = ButtonDefaults.buttonColors().copy(
                                    containerColor = selectedColor
                                ),
                            )
                        }
                    }
                }
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Controls() {
        Column(
            modifier = Modifier.width(IntrinsicSize.Max).padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val selectedColor = paths.lastOrNull()?.second?.color

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val customColorBorder by animateColorAsState(
                    targetValue = if (isCustomColorSelected) {
                        MaterialTheme.colorScheme.onBackground
                    } else {
                        Color.Transparent
                    },
                    label = "custom color"
                )
                val infiniteTransition = rememberInfiniteTransition("rotation")
                val angle by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 7000, easing = LinearEasing)
                    ),
                    label = "angle"
                )

                val hapticFeedback = LocalHapticFeedback.current
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .border(width = 0.8.dp, color = customColorBorder, shape = CircleShape)
                        .padding(2.dp)
                        .drawBehind {
                            val brush = Brush.sweepGradient(
                                colors = gradientColors,
                                center = center
                            )

                            rotate(angle) {
                                drawCircle(
                                    brush = brush,
                                    radius = size.width / 2f,
                                    blendMode = BlendMode.SrcIn
                                )
                            }
                        }
                        .combinedClickable(
                            onClick = {
                                if (customColor.isUnspecified) {
                                    dialogVisible = true
                                } else {
                                    isCustomColorSelected = true
                                    applyColor(customColor)
                                }
                            },
                            onLongClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                dialogVisible = true
                            }
                        )
                )

                colors.fastForEach { color ->
                    val borderColor by animateColorAsState(
                        targetValue = if (selectedColor == color && isCustomColorSelected.not()) {
                            MaterialTheme.colorScheme.onBackground
                        } else {
                            Color.Transparent
                        },
                        label = ""
                    )

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(width = 0.8.dp, color = borderColor, shape = CircleShape)
                            .padding(2.dp)
                            .background(color = color, shape = CircleShape)
                            .clickable {
                                isCustomColorSelected = false
                                applyColor(color)
                            }
                    )
                }
            }
        }
    }

    private fun applyColor(color: Color) {
        paths.add(
            Path() to Paint().apply {
                this.color = color
                strokeWidth = 10f
                style = PaintingStyle.Stroke
                isAntiAlias = true
            }
        )
    }

    @Composable
    private fun Color.getContrast(): State<Color> {
        val luminance = 0.2126f * red + 0.7152f * green + 0.0722f * blue

        return animateColorAsState(
            targetValue = if (luminance > 0.5f) Color.Black else Color.White,
            label = "",
            animationSpec = remember { tween(400) }
        )
    }
}