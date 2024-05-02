package org.example.project.editor.transformation

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Stable
interface Transformation {

    suspend fun save(): Bitmap

    @Composable
    fun Content()
}