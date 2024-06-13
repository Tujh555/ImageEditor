package org.example.project.editor.transformation.crop.cropper.util

import androidx.compose.foundation.layout.BoxWithConstraintsScope
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
