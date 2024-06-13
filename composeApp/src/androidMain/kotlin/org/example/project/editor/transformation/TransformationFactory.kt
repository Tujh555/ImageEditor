package org.example.project.editor.transformation

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap

@Stable
interface TransformationFactory {
    val iconRes: Int
        @DrawableRes get

    fun createWithBitmap(bitmap: ImageBitmap): Transformation
}