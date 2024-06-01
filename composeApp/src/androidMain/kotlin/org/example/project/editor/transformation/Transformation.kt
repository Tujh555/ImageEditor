package org.example.project.editor.transformation

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Stable
interface Transformation {
    fun save(): Bitmap

    fun undo()

    fun clear()

    @Composable
    fun Content()

    @Composable
    fun Controls()
}