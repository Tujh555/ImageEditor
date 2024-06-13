package org.example.project.editor.transformation.crop.cropper.state

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.positionChangeIgnoreConsumed
import androidx.compose.ui.unit.IntSize
import org.example.project.editor.transformation.crop.cropper.TouchRegion
import org.example.project.editor.transformation.crop.cropper.model.AspectRatio
import org.example.project.editor.transformation.crop.cropper.settings.CropProperties
import kotlinx.coroutines.coroutineScope
import kotlin.math.roundToInt

class DynamicCropState internal constructor(
    private var handleSize: Float,
    imageSize: IntSize,
    containerSize: IntSize,
    drawAreaSize: IntSize,
    aspectRatio: AspectRatio,
    overlayRatio: Float,
    maxZoom: Float,
    fling: Boolean,
    zoomable: Boolean,
    pannable: Boolean,
    rotatable: Boolean,
    limitPan: Boolean,
    private val fixedAspectRatio: Boolean,
    private val minDimension: IntSize?
) : CropState(
    imageSize = imageSize,
    containerSize = containerSize,
    drawAreaSize = drawAreaSize,
    aspectRatio = aspectRatio,
    overlayRatio = overlayRatio,
    maxZoom = maxZoom,
    fling = fling,
    zoomable = zoomable,
    pannable = pannable,
    rotatable = rotatable,
    limitPan = limitPan
) {

    private val rectBounds = Rect(
        offset = Offset.Zero,
        size = Size(containerSize.width.toFloat(), containerSize.height.toFloat())
    )

    private var rectTemp = Rect.Zero

    private var distanceToEdgeFromTouch = Offset.Zero

    private var doubleTapped = false

    private var gestureInvoked = false

    override suspend fun updateProperties(cropProperties: CropProperties, forceUpdate: Boolean) {
        handleSize = cropProperties.handleSize
        super.updateProperties(cropProperties, forceUpdate)
    }

    override suspend fun onDown(change: PointerInputChange) {

        rectTemp = overlayRect.copy()

        val position = change.position
        val touchPositionScreenX = position.x
        val touchPositionScreenY = position.y

        val touchPositionOnScreen = Offset(touchPositionScreenX, touchPositionScreenY)

        touchRegion = getTouchRegion(
            position = touchPositionOnScreen,
            rect = overlayRect,
            threshold = handleSize
        )

        distanceToEdgeFromTouch =
            getDistanceToEdgeFromTouch(touchRegion, rectTemp, touchPositionOnScreen)
    }

    override suspend fun onMove(changes: List<PointerInputChange>) {

        if (changes.isEmpty()) {
            touchRegion = TouchRegion.None
            return
        }

        gestureInvoked = changes.size > 1 && (touchRegion == TouchRegion.Inside)

        if (touchRegion != TouchRegion.None && changes.size == 1 && !gestureInvoked) {

            val change = changes.first()

            val doubleHandleSize = handleSize * 2
            val defaultMinDimension =
                IntSize(doubleHandleSize.roundToInt(), doubleHandleSize.roundToInt())

            val newRect = updateOverlayRect(
                distanceToEdgeFromTouch = distanceToEdgeFromTouch,
                touchRegion = touchRegion,
                minDimension = minDimension ?: defaultMinDimension,
                rectTemp = rectTemp,
                overlayRect = overlayRect,
                change = change,
                aspectRatio = getAspectRatio(),
                fixedAspectRatio = fixedAspectRatio,
            )

            snapOverlayRectTo(newRect)
        }
    }

    private fun getAspectRatio(): Float {
        return if (aspectRatio == AspectRatio.Original) {
            imageSize.width / imageSize.height.toFloat()
        } else {
            aspectRatio.value
        }
    }

    override suspend fun onUp(change: PointerInputChange) = coroutineScope {
        if (touchRegion != TouchRegion.None) {

            val isInContainerBounds = isRectInContainerBounds(overlayRect)
            if (!isInContainerBounds) {

                rectTemp = calculateOverlayRectInBounds(rectBounds, overlayRect)

                animateOverlayRectTo(rectTemp)
            }

            animateTransformationToOverlayBounds(overlayRect, true)

            drawAreaRect = updateImageDrawRectFromTransformation()

            touchRegion = TouchRegion.None
        }

        gestureInvoked = false
    }

    override suspend fun onGesture(
        centroid: Offset,
        panChange: Offset,
        zoomChange: Float,
        rotationChange: Float,
        mainPointer: PointerInputChange,
        changes: List<PointerInputChange>
    ) {

        if (touchRegion == TouchRegion.None || gestureInvoked) {
            doubleTapped = false

            val newPan = if (gestureInvoked) Offset.Zero else panChange

            updateTransformState(
                centroid = centroid,
                zoomChange = zoomChange,
                panChange = newPan,
                rotationChange = rotationChange
            )

            drawAreaRect = updateImageDrawRectFromTransformation()

            if (pannable && fling) {
                if (changes.size == 1) {
                    addPosition(mainPointer.uptimeMillis, mainPointer.position)
                }
            }
        }
    }

    override suspend fun onGestureStart() = Unit

    override suspend fun onGestureEnd(onBoundsCalculated: () -> Unit) {

        if (touchRegion == TouchRegion.None || gestureInvoked) {

            if (!doubleTapped) {

                if (pannable && fling && !gestureInvoked && zoom > 1) {
                    fling {
                        drawAreaRect = updateImageDrawRectFromTransformation()
                        onBoundsCalculated()
                    }
                } else {
                    onBoundsCalculated()
                }

                animateTransformationToOverlayBounds(overlayRect, animate = true)
            }
        }
    }

    override suspend fun onDoubleTap(
        offset: Offset,
        zoom: Float,
        onAnimationEnd: () -> Unit
    ) {
        doubleTapped = true

        if (fling) {
            resetTracking()
        }
        resetWithAnimation(pan = pan, zoom = zoom, rotation = rotation)

        drawAreaRect = updateImageDrawRectFromTransformation()


        if (!isOverlayInImageDrawBounds()) {
            animateOverlayRectTo(
                getOverlayFromAspectRatio(
                    containerSize.width.toFloat(),
                    containerSize.height.toFloat(),
                    drawAreaSize.width.toFloat(),
                    aspectRatio,
                    overlayRatio
                )
            )

            animateTransformationToOverlayBounds(overlayRect, false)
        }
        onAnimationEnd()
    }


    private fun calculateOverlayRectInBounds(rectBounds: Rect, rectCurrent: Rect): Rect {

        var width = rectCurrent.width
        var height = rectCurrent.height

        if (width > rectBounds.width) {
            width = rectBounds.width
        }

        if (height > rectBounds.height) {
            height = rectBounds.height
        }

        var rect = Rect(offset = rectCurrent.topLeft, size = Size(width, height))

        if (rect.left < rectBounds.left) {
            rect = rect.translate(rectBounds.left - rect.left, 0f)
        }

        if (rect.top < rectBounds.top) {
            rect = rect.translate(0f, rectBounds.top - rect.top)
        }

        if (rect.right > rectBounds.right) {
            rect = rect.translate(rectBounds.right - rect.right, 0f)
        }

        if (rect.bottom > rectBounds.bottom) {
            rect = rect.translate(0f, rectBounds.bottom - rect.bottom)
        }

        return rect
    }

    private fun updateOverlayRect(
        distanceToEdgeFromTouch: Offset,
        touchRegion: TouchRegion,
        minDimension: IntSize,
        rectTemp: Rect,
        overlayRect: Rect,
        change: PointerInputChange,
        aspectRatio: Float,
        fixedAspectRatio: Boolean,
    ): Rect {

        val position = change.position

        val screenPositionX = position.x + distanceToEdgeFromTouch.x
        val screenPositionY = position.y + distanceToEdgeFromTouch.y

        return when (touchRegion) {

            TouchRegion.TopLeft -> {

                val left = screenPositionX.coerceAtMost(rectTemp.right - minDimension.width)
                val top = if (fixedAspectRatio) {
                    val width = rectTemp.right - left
                    val height = width / aspectRatio
                    rectTemp.bottom - height
                } else {
                    screenPositionY.coerceAtMost(rectTemp.bottom - minDimension.height)
                }
                Rect(
                    left = left,
                    top = top,
                    right = rectTemp.right,
                    bottom = rectTemp.bottom
                )
            }

            TouchRegion.BottomLeft -> {

                val left = screenPositionX.coerceAtMost(rectTemp.right - minDimension.width)
                val bottom = if (fixedAspectRatio) {

                    val width = rectTemp.right - left
                    val height = width / aspectRatio
                    rectTemp.top + height
                } else {
                    screenPositionY.coerceAtLeast(rectTemp.top + minDimension.height)
                }
                Rect(
                    left = left,
                    top = rectTemp.top,
                    right = rectTemp.right,
                    bottom = bottom,
                )
            }

            TouchRegion.TopRight -> {
                val right = screenPositionX.coerceAtLeast(rectTemp.left + minDimension.width)
                val top = if (fixedAspectRatio) {
                    val width = right - rectTemp.left
                    val height = width / aspectRatio
                    rectTemp.bottom - height
                } else {
                    screenPositionY.coerceAtMost(rectTemp.bottom - minDimension.height)
                }

                Rect(
                    left = rectTemp.left,
                    top = top,
                    right = right,
                    bottom = rectTemp.bottom,
                )

            }

            TouchRegion.BottomRight -> {
                val right = screenPositionX.coerceAtLeast(rectTemp.left + minDimension.width)
                val bottom = if (fixedAspectRatio) {
                    val width = right - rectTemp.left
                    val height = width / aspectRatio
                    rectTemp.top + height
                } else {
                    screenPositionY.coerceAtLeast(rectTemp.top + minDimension.height)
                }

                Rect(
                    left = rectTemp.left,
                    top = rectTemp.top,
                    right = right,
                    bottom = bottom
                )
            }

            TouchRegion.Inside -> {
                val drag = change.positionChangeIgnoreConsumed()
                val scaledDragX = drag.x
                val scaledDragY = drag.y
                overlayRect.translate(scaledDragX, scaledDragY)
            }

            else -> overlayRect
        }
    }

    private fun getTouchRegion(
        position: Offset,
        rect: Rect,
        threshold: Float
    ): TouchRegion {

        val closedTouchRange = -threshold / 2..threshold

        return when {
            position.x - rect.left in closedTouchRange &&
                    position.y - rect.top in closedTouchRange -> TouchRegion.TopLeft

            rect.right - position.x in closedTouchRange &&
                    position.y - rect.top in closedTouchRange -> TouchRegion.TopRight

            rect.right - position.x in closedTouchRange &&
                    rect.bottom - position.y in closedTouchRange -> TouchRegion.BottomRight

            position.x - rect.left in closedTouchRange &&
                    rect.bottom - position.y in closedTouchRange -> TouchRegion.BottomLeft


            rect.contains(offset = position) -> TouchRegion.Inside
            else -> TouchRegion.None
        }
    }

    private fun getDistanceToEdgeFromTouch(
        touchRegion: TouchRegion,
        rect: Rect,
        touchPosition: Offset
    ) = when (touchRegion) {
        TouchRegion.TopLeft -> {
            rect.topLeft - touchPosition
        }
        TouchRegion.TopRight -> {
            rect.topRight - touchPosition
        }
        TouchRegion.BottomLeft -> {
            rect.bottomLeft - touchPosition
        }
        TouchRegion.BottomRight -> {
            rect.bottomRight - touchPosition
        }
        else -> {
            Offset.Zero
        }
    }
}
