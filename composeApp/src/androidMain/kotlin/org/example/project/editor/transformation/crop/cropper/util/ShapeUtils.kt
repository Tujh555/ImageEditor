package org.example.project.editor.transformation.crop.cropper.util

import android.graphics.Matrix
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.unit.LayoutDirection
import org.example.project.editor.transformation.crop.cropper.model.AspectRatio
import kotlin.math.cos
import kotlin.math.sin

fun createPolygonPath(cx: Float, cy: Float, sides: Int, radius: Float): Path {

    val angle = 2.0 * Math.PI / sides

    return Path().apply {
        moveTo(
            cx + (radius * cos(0.0)).toFloat(),
            cy + (radius * sin(0.0)).toFloat()
        )
        for (i in 1 until sides) {
            lineTo(
                cx + (radius * cos(angle * i)).toFloat(),
                cy + (radius * sin(angle * i)).toFloat()
            )
        }
        close()
    }
}

fun createPolygonShape(sides: Int, degrees: Float = 0f): GenericShape {
    return GenericShape { size: Size, _: LayoutDirection ->

        val radius = size.width.coerceAtMost(size.height) / 2
        addPath(
            createPolygonPath(
                cx = size.width / 2,
                cy = size.height / 2,
                sides = sides,
                radius = radius
            )
        )
        val matrix = Matrix()
        matrix.postRotate(degrees, size.width / 2, size.height / 2)
        this.asAndroidPath().transform(matrix)
    }
}

fun createRectShape(aspectRatio: AspectRatio): GenericShape {
    return GenericShape { size: Size, _: LayoutDirection ->
        val value = aspectRatio.value

        val width = size.width
        val height = size.height
        val shapeSize =
            if (aspectRatio == AspectRatio.Original) Size(width, height)
            else if (value > 1) Size(width = width, height = width / value)
            else Size(width = height * value, height = height)

        addRect(Rect(offset = Offset.Zero, size = shapeSize))
    }
}

fun Path.scaleAndTranslatePath(
    width: Float,
    height: Float,
) {
    val pathSize = getBounds().size

    val matrix = Matrix()
    matrix.postScale(
        width / pathSize.width,
        height / pathSize.height
    )

    this.asAndroidPath().transform(matrix)

    val left = getBounds().left
    val top = getBounds().top

    translate(Offset(-left, -top))
}