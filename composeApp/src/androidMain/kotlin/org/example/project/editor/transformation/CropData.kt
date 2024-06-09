package org.example.project.editor.transformation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize
import org.example.project.editor.transformation.crop.cropper.model.CropOutline

@Immutable
data class CropData(
    val scaledImageBitmap: ImageBitmap,
    val cropRect: Rect,
    val cropOutline: CropOutline,
    val requiredSize: IntSize?,
)
