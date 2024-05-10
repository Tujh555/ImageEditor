package org.example.project.editor.transformation

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Stable
interface Transformation {
    val iconRes: Int
        @DrawableRes get

    fun save(): Bitmap

    @Composable
    fun Content()

    @Composable
    fun Controls()
}