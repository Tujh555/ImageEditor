package org.example.project.editor.transformation

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.ImageBitmap

interface TransformationFactory {
    val iconRes: Int
        @DrawableRes get

    fun createWithBitmap(bitmap: ImageBitmap): Transformation
}