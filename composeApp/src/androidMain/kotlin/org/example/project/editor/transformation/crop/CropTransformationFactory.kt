package org.example.project.editor.transformation.crop

import androidx.compose.ui.graphics.ImageBitmap
import org.example.project.R
import org.example.project.editor.transformation.Transformation
import org.example.project.editor.transformation.TransformationFactory

class CropTransformationFactory : TransformationFactory {
    override val iconRes: Int = R.drawable.ic_crop

    override fun createWithBitmap(bitmap: ImageBitmap): Transformation = CropTransformation(bitmap)
}