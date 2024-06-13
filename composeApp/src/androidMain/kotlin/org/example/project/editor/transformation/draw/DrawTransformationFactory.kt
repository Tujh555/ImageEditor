package org.example.project.editor.transformation.draw

import androidx.compose.ui.graphics.ImageBitmap
import org.example.project.R
import org.example.project.editor.transformation.Transformation
import org.example.project.editor.transformation.TransformationFactory

internal class DrawTransformationFactory : TransformationFactory {
    override val iconRes: Int
        get() = R.drawable.ic_brush

    override fun createWithBitmap(bitmap: ImageBitmap): Transformation = DrawTransformation(bitmap)
}