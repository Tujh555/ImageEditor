package org.example.project.editor.transformation.crop

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toOffset
import org.example.project.editor.transformation.Transformation

internal class CropTransformation(private val initialBitmap: ImageBitmap) : Transformation {
    private val contentScale = ContentScale.Fit

    override fun save(): Bitmap {
        TODO("Not yet implemented")
    }

    override fun undo() {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    @Composable
    override fun Content() {
        BoxWithConstraints(
            modifier = Modifier.clipToBounds(),
            contentAlignment = Alignment.Center
        ) {
            val bitmapWidth = initialBitmap.width
            val bitmapHeight = initialBitmap.height

            val (boxWidth: Int, boxHeight: Int) = getParentSize(bitmapWidth, bitmapHeight)

            val srcSize = Size(bitmapWidth.toFloat(), bitmapHeight.toFloat())
            val dstSize = Size(boxWidth.toFloat(), boxHeight.toFloat())

            val scaleFactor = contentScale.computeScaleFactor(srcSize, dstSize)

            val imageWidth = bitmapWidth * scaleFactor.scaleX
            val imageHeight = bitmapHeight * scaleFactor.scaleY

            val containerWidth: Dp
            val containerHeight: Dp
            val imageWidthPx: Int
            val imageHeightPx: Int

            with(LocalDensity.current) {
                containerWidth = constraints.maxWidth.toDp()
                containerHeight = constraints.maxHeight.toDp()
                imageWidthPx = imageWidth
                    .coerceAtMost(boxWidth.toFloat())
                    .toDp()
                    .roundToPx()

                imageHeightPx = imageHeight
                    .coerceAtMost(boxHeight.toFloat())
                    .toDp()
                    .roundToPx()
            }

            val drawTopLeft = IntOffset(
                x = (constraints.maxWidth - imageWidthPx) / 2,
                y = (constraints.maxHeight - imageHeightPx) / 2
            )
            val dstDrawSize = IntSize(imageWidthPx, imageHeightPx)
            val srcDrawSize = IntSize(initialBitmap.width, initialBitmap.height)
            val drawRect = Rect(
                topLeft = drawTopLeft.toOffset(),
                bottomRight = Offset(
                    x = drawTopLeft.x + dstDrawSize.width.toFloat(),
                    y = drawTopLeft.y + dstDrawSize.height.toFloat()
                ),
            )

            var gridRect by remember(drawRect) {
                mutableStateOf(drawRect)
            }

            var canvasPosition by remember {
                mutableStateOf(Offset.Zero)
            }

            Canvas(
                modifier = Modifier
                    .size(containerWidth, containerHeight)
                    .onGloballyPositioned {
                        canvasPosition = it.positionInParent()
                    }
            ) {
                drawImage(
                    image = initialBitmap,
                    srcSize = srcDrawSize,
                    dstSize = dstDrawSize,
                    dstOffset = drawTopLeft
                )

                drawGrid(
                    rect = gridRect,
                    strokeWidth = 3.0f,
                    color = Color.Red,
                )
                drawRect(
                    color = Color.Red,
                    topLeft = drawRect.topLeft,
                    size = drawRect.size,
                    style = Stroke(
                        width = 3.0f,
                    ),
                )
            }

            val boxSize = with(LocalDensity.current) {
                gridRect.size.toDpSize()
            }
            Box(
                modifier = Modifier
                    .size(boxSize)
                    .offset { canvasPosition.round() }
                    .pointerInput(containerWidth, containerHeight) {
                        detectDragGestures(
                            onDragStart = {
                                Log.d("--tag", "drag offset = $it")
                                Log.d("--tag", "grid topLeft = ${gridRect.topLeft}")
                                Log.d("--tag", "grid bottomRight = ${gridRect.bottomRight}")
                            },
                            onDragEnd = {

                            },
                            onDragCancel = {

                            },
                            onDrag = { change: PointerInputChange, dragAmount: Offset ->

                            }
                        )
                    }
                    .border(width = 1.dp, color = Color.Green)
            )
        }
    }

    @Composable
    override fun Controls() = Unit

}