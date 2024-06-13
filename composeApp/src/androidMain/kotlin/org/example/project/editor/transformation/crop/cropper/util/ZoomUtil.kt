package org.example.project.editor.transformation.crop.cropper.util

import androidx.compose.ui.graphics.GraphicsLayerScope
import org.example.project.editor.transformation.crop.cropper.state.TransformState

internal fun getNextZoomLevel(zoomLevel: ZoomLevel): ZoomLevel = when (zoomLevel) {
    ZoomLevel.Mid -> {
        ZoomLevel.Max
    }
    ZoomLevel.Max -> {
        ZoomLevel.Min
    }
    else -> {
        ZoomLevel.Mid
    }
}

internal fun GraphicsLayerScope.update(transformState: TransformState) {

    val zoom = transformState.zoom
    this.scaleX = zoom
    this.scaleY = zoom

    val pan = transformState.pan
    val translationX = pan.x
    val translationY = pan.y

    this.translationX = translationX
    this.translationY = translationY

    this.rotationZ = transformState.rotation
}
