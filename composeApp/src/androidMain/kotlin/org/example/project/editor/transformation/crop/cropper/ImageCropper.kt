package org.example.project.editor.transformation.crop.cropper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import org.example.project.editor.transformation.crop.cropper.draw.DrawingOverlay
import org.example.project.editor.transformation.crop.cropper.draw.ImageDrawCanvas
import org.example.project.editor.transformation.crop.cropper.image.ImageWithConstraints
import org.example.project.editor.transformation.crop.cropper.image.getScaledImageBitmap
import org.example.project.editor.transformation.crop.cropper.model.CropOutline
import org.example.project.editor.transformation.crop.cropper.settings.CropProperties
import org.example.project.editor.transformation.crop.cropper.settings.CropStyle
import org.example.project.editor.transformation.crop.cropper.state.DynamicCropState
import org.example.project.editor.transformation.crop.cropper.state.rememberCropState
import kotlinx.coroutines.delay
import org.example.project.editor.transformation.CropData
import org.example.project.editor.transformation.crop.cropper.state.CropState

@Composable
fun ImageCropper(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    contentDescription: String?,
    cropStyle: CropStyle,
    cropProperties: CropProperties,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    backgroundColor: Color = Color.Black,
    onCropDataChanged: (CropData) -> Unit,
    onCropStateChanged: (CropState) -> Unit,
    onDrawGrid: (DrawScope.(rect: Rect, strokeWidth: Float, color: Color) -> Unit)? = null,
) {

    ImageWithConstraints(
        modifier = modifier.clipToBounds(),
        contentScale = cropProperties.contentScale,
        contentDescription = contentDescription,
        filterQuality = filterQuality,
        imageBitmap = imageBitmap,
        drawImage = false
    ) {

        val scaledImageBitmap = getScaledImageBitmap(
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            rect = rect,
            bitmap = imageBitmap,
            contentScale = cropProperties.contentScale,
        )

        val containerWidthPx = constraints.maxWidth
        val containerHeightPx = constraints.maxHeight

        val containerWidth: Dp
        val containerHeight: Dp

        val bitmapWidth = scaledImageBitmap.width
        val bitmapHeight = scaledImageBitmap.height

        val imageWidthPx: Int
        val imageHeightPx: Int

        with(LocalDensity.current) {
            imageWidthPx = imageWidth.roundToPx()
            imageHeightPx = imageHeight.roundToPx()
            containerWidth = containerWidthPx.toDp()
            containerHeight = containerHeightPx.toDp()
        }

        val contentScale = cropProperties.contentScale
        val fixedAspectRatio = cropProperties.fixedAspectRatio
        val cropOutline = cropProperties.cropOutlineProperty.cropOutline

        val resetKeys =
            getResetKeys(
                scaledImageBitmap,
                imageWidthPx,
                imageHeightPx,
                contentScale,
                fixedAspectRatio
            )

        val cropState = rememberCropState(
            imageSize = IntSize(bitmapWidth, bitmapHeight),
            containerSize = IntSize(containerWidthPx, containerHeightPx),
            drawAreaSize = IntSize(imageWidthPx, imageHeightPx),
            cropProperties = cropProperties,
            keys = resetKeys
        )

        LaunchedEffect(cropState) {
            onCropStateChanged(cropState)
        }

        val isHandleTouched by remember(cropState) {
            derivedStateOf {
                cropState is DynamicCropState && handlesTouched(cropState.touchRegion)
            }
        }

        val pressedStateColor = remember(cropStyle.backgroundColor){
            cropStyle.backgroundColor
                .copy(cropStyle.backgroundColor.alpha * .7f)
        }

        val transparentColor by animateColorAsState(
            animationSpec = tween(300, easing = LinearEasing),
            targetValue = if (isHandleTouched) pressedStateColor else cropStyle.backgroundColor
        )

        val cropData = remember(
            scaledImageBitmap,
            cropState.cropRect,
            cropOutline,
            cropProperties.requiredSize
        ) {
            CropData(
                scaledImageBitmap = scaledImageBitmap,
                cropRect = cropState.cropRect,
                cropOutline = cropOutline,
                requiredSize = cropProperties.requiredSize,
            )
        }

        LaunchedEffect(cropData) {
            onCropDataChanged(cropData)
        }

        val imageModifier = Modifier
            .size(containerWidth, containerHeight)
            .crop(
                keys = resetKeys,
                cropState = cropState
            )

        LaunchedEffect(key1 = cropProperties) {
            cropState.updateProperties(cropProperties)
        }


        ImageCropper(
            modifier = imageModifier,
            visible = true,
            imageBitmap = imageBitmap,
            containerWidth = containerWidth,
            containerHeight = containerHeight,
            imageWidthPx = imageWidthPx,
            imageHeightPx = imageHeightPx,
            handleSize = cropProperties.handleSize,
            overlayRect = cropState.overlayRect,
            cropOutline = cropOutline,
            cropStyle = cropStyle,
            transparentColor = transparentColor,
            backgroundColor = backgroundColor,
            onDrawGrid = onDrawGrid,
        )
    }
}

@Composable
private fun ImageCropper(
    modifier: Modifier,
    visible: Boolean,
    imageBitmap: ImageBitmap,
    containerWidth: Dp,
    containerHeight: Dp,
    imageWidthPx: Int,
    imageHeightPx: Int,
    handleSize: Float,
    cropOutline: CropOutline,
    cropStyle: CropStyle,
    overlayRect: Rect,
    transparentColor: Color,
    backgroundColor: Color,
    onDrawGrid: (DrawScope.(rect: Rect, strokeWidth: Float, color: Color) -> Unit)?,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(tween(500))
        ) {

            ImageCropperImpl(
                modifier = modifier,
                imageBitmap = imageBitmap,
                containerWidth = containerWidth,
                containerHeight = containerHeight,
                imageWidthPx = imageWidthPx,
                imageHeightPx = imageHeightPx,
                cropOutline = cropOutline,
                handleSize = handleSize,
                cropStyle = cropStyle,
                rectOverlay = overlayRect,
                transparentColor = transparentColor,
                onDrawGrid = onDrawGrid,
            )
        }
    }
}

@Composable
private fun ImageCropperImpl(
    modifier: Modifier,
    imageBitmap: ImageBitmap,
    containerWidth: Dp,
    containerHeight: Dp,
    imageWidthPx: Int,
    imageHeightPx: Int,
    cropOutline: CropOutline,
    handleSize: Float,
    cropStyle: CropStyle,
    transparentColor: Color,
    rectOverlay: Rect,
    onDrawGrid: (DrawScope.(rect: Rect, strokeWidth: Float, color: Color) -> Unit)?,
) {

    Box(contentAlignment = Alignment.Center) {

        ImageDrawCanvas(
            modifier = modifier,
            imageBitmap = imageBitmap,
            imageWidth = imageWidthPx,
            imageHeight = imageHeightPx
        )

        val drawOverlay = cropStyle.drawOverlay

        val drawGrid = cropStyle.drawGrid
        val overlayColor = cropStyle.overlayColor
        val handleColor = cropStyle.handleColor
        val strokeWidth = cropStyle.strokeWidth

        DrawingOverlay(
            modifier = Modifier.size(containerWidth, containerHeight),
            drawOverlay = drawOverlay,
            rect = rectOverlay,
            cropOutline = cropOutline,
            drawGrid = drawGrid,
            overlayColor = overlayColor,
            handleColor = handleColor,
            strokeWidth = strokeWidth,
            handleSize = handleSize,
            transparentColor = transparentColor,
            onDrawGrid = onDrawGrid,
        )

    }
}

@Composable
private fun getResetKeys(
    scaledImageBitmap: ImageBitmap,
    imageWidthPx: Int,
    imageHeightPx: Int,
    contentScale: ContentScale,
    fixedAspectRatio: Boolean,
) = remember(
    scaledImageBitmap,
    imageWidthPx,
    imageHeightPx,
    contentScale,
    fixedAspectRatio,
) {
    arrayOf(
        scaledImageBitmap,
        imageWidthPx,
        imageHeightPx,
        contentScale,
        fixedAspectRatio,
    )
}