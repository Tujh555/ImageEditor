package org.example.project.editor.transformation.draw

import android.graphics.Bitmap
import android.graphics.Picture
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.example.project.editor.ComposeCanvas
import org.example.project.editor.modifiers.capturable.DrawableCatcher
import org.example.project.editor.modifiers.capturable.drawable
import org.example.project.editor.modifiers.gestures.MotionEvent
import org.example.project.editor.modifiers.gestures.onOneTouchEvents
import org.example.project.editor.transformation.Transformation

class DrawTransformation(private val imagePath: Uri) : Transformation {
    private val drawPath = Path()
    private val pathPaint = Paint().apply {
        color = Color.Green
        strokeWidth = 10f // TODO get from ui
        style = PaintingStyle.Stroke
    }
    private val catcher = DrawableCatcher()
    private var previousPosition by mutableStateOf(Offset.Unspecified)
    private var drawRect by mutableStateOf(Rect.Zero)
    private var motionEvent by mutableStateOf<MotionEvent>(MotionEvent.Unspecified)

    override suspend fun save(): Bitmap {
        return catcher.captureAsync().await()
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
                    .drawable(catcher)
                    .border(width = 1.dp, color = Color.Green),
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
                        .crossfade(true)
                        .build(),
                    contentDescription = null
                )
            }
        }
    }

    private fun drawOnCanvas(canvas: ComposeCanvas) {
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

            MotionEvent.Unspecified -> {
                // just ignore
            }
        }

        previousPosition = motionEvent.position

        canvas.drawPath(
            path = drawPath,
            paint = pathPaint
        )
    }

}