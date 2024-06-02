package org.example.project.editor.transformation.crop

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize

internal fun getScaledBitmapRect(
    boxWidth: Int,
    boxHeight: Int,
    imageWidth: Float,
    imageHeight: Float,
    bitmapWidth: Int,
    bitmapHeight: Int
): IntRect {
    val scaledBitmapX = boxWidth / imageWidth
    val scaledBitmapY = boxHeight / imageHeight

    val topLeft = IntOffset(
        x = (bitmapWidth * (imageWidth - boxWidth) / imageWidth / 2)
            .coerceAtLeast(0f).toInt(),
        y = (bitmapHeight * (imageHeight - boxHeight) / imageHeight / 2)
            .coerceAtLeast(0f).toInt()
    )

    val size = IntSize(
        width = (bitmapWidth * scaledBitmapX).toInt().coerceAtMost(bitmapWidth),
        height = (bitmapHeight * scaledBitmapY).toInt().coerceAtMost(bitmapHeight)
    )

    return IntRect(offset = topLeft, size = size)
}

internal fun BoxWithConstraintsScope.getParentSize(
    bitmapWidth: Int,
    bitmapHeight: Int
): IntSize {

    val hasBoundedDimens = constraints.hasBoundedWidth && constraints.hasBoundedHeight

    val hasFixedDimens = constraints.hasFixedWidth && constraints.hasFixedHeight

    val boxWidth: Int = if (hasBoundedDimens || hasFixedDimens) {
        constraints.maxWidth
    } else {
        constraints.minWidth.coerceAtLeast(bitmapWidth)
    }
    val boxHeight: Int = if (hasBoundedDimens || hasFixedDimens) {
        constraints.maxHeight
    } else {
        constraints.minHeight.coerceAtLeast(bitmapHeight)
    }
    return IntSize(boxWidth, boxHeight)
}

fun DrawScope.drawGrid(rect: Rect, strokeWidth: Float, color: Color) {

    val width = rect.width
    val height = rect.height
    val gridWidth = width / 3
    val gridHeight = height / 3

    // Horizontal lines
    for (i in 1..2) {
        drawLine(
            color = color,
            start = Offset(rect.left, rect.top + i * gridHeight),
            end = Offset(rect.right, rect.top + i * gridHeight),
            strokeWidth = strokeWidth
        )
    }

    // Vertical lines
    for (i in 1..2) {
        drawLine(
            color,
            start = Offset(rect.left + i * gridWidth, rect.top),
            end = Offset(rect.left + i * gridWidth, rect.bottom),
            strokeWidth = strokeWidth
        )
    }


}