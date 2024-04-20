package org.example.project.editor.transformation.draw

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import org.example.project.editor.ComposeCanvas
import org.example.project.editor.gestures.MotionEvent
import org.example.project.editor.gestures.onOneTouchEvents
import tech.inno.dion.chat.image.editor.transformation.Transformation

internal class DrawTransformation : Transformation {
    private val drawPath = Path()
    private val motionEvent = mutableStateOf<MotionEvent>(MotionEvent.Unspecified)
    private val previousPosition = mutableStateOf(Offset.Unspecified)
    private val pathPaint = Paint().apply {
        color = Color.Green
        strokeWidth = 10f
        style = PaintingStyle.Stroke
    }

    context(DrawScope)
    override fun drawOnCanvas(canvas: ComposeCanvas) {
        when (motionEvent.value) {
            is MotionEvent.Down -> drawPath.moveTo(motionEvent.value.x, motionEvent.value.y)

            is MotionEvent.Move -> if (previousPosition.value.isUnspecified.not()) {
                drawPath
                    .quadraticTo(
                        x1 = previousPosition.value.x,
                        y1 = previousPosition.value.y,
                        x2 = (previousPosition.value.x + motionEvent.value.x) / 2,
                        y2 = (previousPosition.value.y + motionEvent.value.y) / 2
                    )
            }

            is MotionEvent.Up -> motionEvent.value = MotionEvent.Unspecified

            MotionEvent.Unspecified -> {
                // just ignore
            }
        }

        previousPosition.value = motionEvent.value.position

        canvas.drawPath(
            path = drawPath,
            paint = pathPaint
        )
    }

    @Composable
    override fun Content(safeRect: Rect) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onOneTouchEvents(key = safeRect) { event ->
                    if (safeRect.contains(event.position)) {
                        motionEvent.value = event
                    }
                }
                .border(width = 1.dp, color = Color.Red)
        )
    }
}