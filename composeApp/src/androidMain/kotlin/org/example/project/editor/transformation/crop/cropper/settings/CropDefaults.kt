package org.example.project.editor.transformation.crop.cropper.settings

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import org.example.project.editor.transformation.crop.cropper.model.AspectRatio
import org.example.project.editor.transformation.crop.cropper.model.CropOutline
import org.example.project.editor.transformation.crop.cropper.model.OutlineType
import org.example.project.editor.transformation.crop.cropper.model.aspectRatios

object CropDefaults {
    fun properties(
        handleSize: Float,
        maxZoom: Float = 10f,
        contentScale: ContentScale = ContentScale.Fit,
        cropOutlineProperty: CropOutlineProperty,
        aspectRatio: AspectRatio = aspectRatios[2].aspectRatio,
        overlayRatio: Float = .9f,
        pannable: Boolean = true,
        fling: Boolean = false,
        zoomable: Boolean = true,
        rotatable: Boolean = false,
        fixedAspectRatio: Boolean = false,
        requiredSize: IntSize? = null,
        minDimension: IntSize? = null,
    ): CropProperties {
        return CropProperties(
            handleSize = handleSize,
            contentScale = contentScale,
            cropOutlineProperty = cropOutlineProperty,
            maxZoom = maxZoom,
            aspectRatio = aspectRatio,
            overlayRatio = overlayRatio,
            pannable = pannable,
            fling = fling,
            zoomable = zoomable,
            rotatable = rotatable,
            fixedAspectRatio = fixedAspectRatio,
            requiredSize = requiredSize,
            minDimension = minDimension,
        )
    }

    fun style(
        drawOverlay: Boolean = true,
        drawGrid: Boolean = true,
        strokeWidth: Dp = 1.dp,
        overlayColor: Color = Color.Gray,
        handleColor: Color = Color.White,
        backgroundColor: Color
    ): CropStyle {
        return CropStyle(
            drawOverlay = drawOverlay,
            drawGrid = drawGrid,
            strokeWidth = strokeWidth,
            overlayColor = overlayColor,
            handleColor = handleColor,
            backgroundColor = backgroundColor
        )
    }
}

@Immutable
data class CropProperties internal constructor(
    val handleSize: Float,
    val contentScale: ContentScale,
    val cropOutlineProperty: CropOutlineProperty,
    val aspectRatio: AspectRatio,
    val overlayRatio: Float,
    val pannable: Boolean,
    val fling: Boolean,
    val rotatable: Boolean,
    val zoomable: Boolean,
    val maxZoom: Float,
    val fixedAspectRatio: Boolean = false,
    val requiredSize: IntSize? = null,
    val minDimension: IntSize? = null,
)

@Immutable
data class CropStyle internal constructor(
    val drawOverlay: Boolean,
    val drawGrid: Boolean,
    val strokeWidth: Dp,
    val overlayColor: Color,
    val handleColor: Color,
    val backgroundColor: Color,
    val cropTheme: CropTheme = CropTheme.Dark
)

@Immutable
data class CropOutlineProperty(
    val outlineType: OutlineType,
    val cropOutline: CropOutline
)

enum class CropTheme{
    Light,
    Dark,
    System
}
