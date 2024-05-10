package org.example.project.editor.transformation.draw

import android.graphics.Bitmap
import android.graphics.Picture
import android.net.Uri
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.example.project.R
import org.example.project.editor.AndroidCanvas
import org.example.project.editor.modifiers.gestures.MotionEvent
import org.example.project.editor.modifiers.gestures.onOneTouchEvents
import org.example.project.editor.transformation.Transformation

class DrawTransformation(private val imagePath: Uri) : Transformation {
    private val drawPath = Path()
    private var previousPosition by mutableStateOf(Offset.Unspecified)
    private var drawRect by mutableStateOf(Rect.Zero)
    private var motionEvent by mutableStateOf<MotionEvent>(MotionEvent.Unspecified)
    private val colors = listOf(
        Color.White,
        Color.Black,
        Color.Red,
        Color.Green,
        Color.Yellow,
    )
    private var selectedColor by mutableStateOf(Color.Green)

    private val pathPaint = Paint().apply {
        color = selectedColor
        strokeWidth = strokeWidth
        style = PaintingStyle.Stroke
    }

    private val picture by mutableStateOf(Picture())
    override val iconRes: Int = R.drawable.ic_brush

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

                        val pictureCanvas = Canvas(picture.beginRecording(width, height))

                        draw(this, this.layoutDirection, pictureCanvas, this.size) {
                            this@drawWithContent.drawContent()
                        }
                        when (motionEvent) {
                            is MotionEvent.Down -> drawPath.moveTo(motionEvent.x, motionEvent.y)

                            is MotionEvent.Move ->
                                if (previousPosition.isUnspecified.not()) {
                                    drawPath.quadraticTo(
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

                        pictureCanvas.drawPath(
                            path = drawPath,
                            paint = pathPaint
                        )

                        picture.endRecording()

                        drawIntoCanvas { canvas ->
                            canvas.nativeCanvas.drawPicture(picture)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    modifier = Modifier
                        .onGloballyPositioned { layoutCoordinates ->
                            val bounds = layoutCoordinates.boundsInParent()
                            if (drawRect != bounds) {
                                drawRect = bounds
                            }
                        },
                    model = ImageRequest
                        .Builder(LocalContext.current)
                        .data(imagePath)
                        .build(),
                    contentDescription = null
                )
            }
        }
    }

    @Composable
    override fun Controls() {
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                colors.fastForEach { color ->
                    val borderColor by animateColorAsState(
                        targetValue = if (selectedColor == color) {
                            MaterialTheme.colorScheme.onBackground
                        } else {
                            Color.Transparent
                        },
                        label = ""
                    )

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(color = color, shape = CircleShape)
                            .border(width = 0.8.dp, color = borderColor, shape = CircleShape)
                            .clickable {
                                selectedColor = color
                                pathPaint.color = color
                            }
                    )
                }
            }
        }
    }
}