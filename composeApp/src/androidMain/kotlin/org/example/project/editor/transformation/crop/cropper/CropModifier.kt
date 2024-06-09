package org.example.project.editor.transformation.crop.cropper

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import org.example.project.editor.transformation.crop.cropper.util.ZoomLevel
import org.example.project.editor.transformation.crop.cropper.util.getNextZoomLevel
import org.example.project.editor.transformation.crop.cropper.util.update
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.editor.transformation.crop.cropper.model.CropData
import org.example.project.editor.transformation.crop.cropper.state.CropState
import org.example.project.editor.transformation.crop.cropper.state.cropData
import kotlin.math.PI
import kotlin.math.abs

@SuppressLint("SuspiciousModifierThen")
fun Modifier.crop(
    vararg keys: Any?,
    cropState: CropState,
    zoomOnDoubleTap: (ZoomLevel) -> Float = cropState.DefaultOnDoubleTap,
    onDown: ((CropData) -> Unit)? = null,
    onMove: ((CropData) -> Unit)? = null,
    onUp: ((CropData) -> Unit)? = null,
    onGestureStart: ((CropData) -> Unit)? = null,
    onGesture: ((CropData) -> Unit)? = null,
    onGestureEnd: ((CropData) -> Unit)? = null
) = composed(

    factory = {

        LaunchedEffect(key1 = cropState){
            cropState.init()
        }

        val coroutineScope = rememberCoroutineScope()

        var zoomLevel by remember { mutableStateOf(ZoomLevel.Min) }

        val transformModifier = Modifier.pointerInput(*keys) {
            detectTransformGestures(
                consume = false,
                onGestureStart = {
                    onGestureStart?.invoke(cropState.cropData)
                },
                onGestureEnd = {
                    coroutineScope.launch {
                        cropState.onGestureEnd {
                            onGestureEnd?.invoke(cropState.cropData)
                        }
                    }
                },
                onGesture = { centroid, pan, zoom, rotate, mainPointer, pointerList ->

                    coroutineScope.launch {
                        cropState.onGesture(
                            centroid = centroid,
                            panChange = pan,
                            zoomChange = zoom,
                            rotationChange = rotate,
                            mainPointer = mainPointer,
                            changes = pointerList
                        )
                    }
                    onGesture?.invoke(cropState.cropData)
                    mainPointer.consume()
                }
            )
        }

        val tapModifier = Modifier.pointerInput(*keys) {
            detectTapGestures(
                onDoubleTap = { offset: Offset ->
                    coroutineScope.launch {
                        zoomLevel = getNextZoomLevel(zoomLevel)
                        val newZoom = zoomOnDoubleTap(zoomLevel)
                        cropState.onDoubleTap(
                            offset = offset,
                            zoom = newZoom
                        ) {
                            onGestureEnd?.invoke(cropState.cropData)
                        }
                    }
                }
            )
        }

        val touchModifier = Modifier.pointerInput(*keys) {
            detectMotionEventsAsList(
                onDown = {
                    coroutineScope.launch {
                        cropState.onDown(it)
                        onDown?.invoke(cropState.cropData)
                    }
                },
                onMove = {
                    coroutineScope.launch {
                        cropState.onMove(it)
                        onMove?.invoke(cropState.cropData)
                    }
                },
                onUp = {
                    coroutineScope.launch {
                        cropState.onUp(it)
                        onUp?.invoke(cropState.cropData)
                    }
                }
            )
        }

        val graphicsModifier = Modifier.graphicsLayer {
            this.update(cropState)
        }

        this.then(
            clipToBounds()
                .then(tapModifier)
                .then(transformModifier)
                .then(touchModifier)
                .then(graphicsModifier)
        )
    },
)

internal val CropState.DefaultOnDoubleTap: (ZoomLevel) -> Float
    get() = { zoomLevel: ZoomLevel ->
        when (zoomLevel) {
            ZoomLevel.Min -> 1f
            ZoomLevel.Mid -> 3f.coerceIn(zoomMin, zoomMax)
            ZoomLevel.Max -> 5f.coerceAtLeast(zoomMax)
        }
    }

suspend fun PointerInputScope.detectMotionEventsAsList(
    onDown: (PointerInputChange) -> Unit = {},
    onMove: (List<PointerInputChange>) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    delayAfterDownInMillis: Long = 0L,
    requireUnconsumed: Boolean = true,
    pass: PointerEventPass = PointerEventPass.Main
) {

    coroutineScope {
        awaitEachGesture {
            val down: PointerInputChange = awaitFirstDown(
                requireUnconsumed = requireUnconsumed,
                pass = pass
            )
            onDown(down)

            var pointer = down
            var pointerId = down.id

            var waitedAfterDown = false

            launch {
                delay(delayAfterDownInMillis)
                waitedAfterDown = true
            }

            while (true) {

                val event: PointerEvent = awaitPointerEvent(pass)

                val anyPressed = event.changes.any { it.pressed }

                if (anyPressed) {
                    val pointerInputChange =
                        event.changes.firstOrNull { it.id == pointerId }
                            ?: event.changes.first()

                    pointerId = pointerInputChange.id
                    pointer = pointerInputChange

                    if (waitedAfterDown) {
                        onMove(event.changes)
                    }

                } else {
                    onUp(pointer)
                    break
                }
            }
        }
    }
}

suspend fun PointerInputScope.detectTransformGestures(
    panZoomLock: Boolean = false,
    consume: Boolean = true,
    pass: PointerEventPass = PointerEventPass.Main,
    onGestureStart: (PointerInputChange) -> Unit = {},
    onGesture: (
        centroid: Offset,
        pan: Offset,
        zoom: Float,
        rotation: Float,
        mainPointer: PointerInputChange,
        changes: List<PointerInputChange>
    ) -> Unit,
    onGestureEnd: (PointerInputChange) -> Unit = {}
) {
    awaitEachGesture {
        var rotation = 0f
        var zoom = 1f
        var pan = Offset.Zero
        var pastTouchSlop = false
        val touchSlop = viewConfiguration.touchSlop
        var lockedToPanZoom = false

        val down: PointerInputChange = awaitFirstDown(
            requireUnconsumed = false,
            pass = pass
        )
        onGestureStart(down)

        var pointer = down
        var pointerId = down.id

        do {
            val event = awaitPointerEvent(pass = pass)
            val canceled =
                event.changes.any { it.isConsumed }

            if (!canceled) {

                val pointerInputChange =
                    event.changes.firstOrNull { it.id == pointerId }
                        ?: event.changes.first()

                pointerId = pointerInputChange.id
                pointer = pointerInputChange

                val zoomChange = event.calculateZoom()
                val rotationChange = event.calculateRotation()
                val panChange = event.calculatePan()

                if (!pastTouchSlop) {
                    zoom *= zoomChange
                    rotation += rotationChange
                    pan += panChange

                    val centroidSize = event.calculateCentroidSize(useCurrent = false)
                    val zoomMotion = abs(1 - zoom) * centroidSize
                    val rotationMotion =
                        abs(rotation * PI.toFloat() * centroidSize / 180f)
                    val panMotion = pan.getDistance()

                    if (zoomMotion > touchSlop ||
                        rotationMotion > touchSlop ||
                        panMotion > touchSlop
                    ) {
                        pastTouchSlop = true
                        lockedToPanZoom = panZoomLock && rotationMotion < touchSlop
                    }
                }

                if (pastTouchSlop) {
                    val centroid = event.calculateCentroid(useCurrent = false)
                    val effectiveRotation = if (lockedToPanZoom) 0f else rotationChange
                    if (effectiveRotation != 0f ||
                        zoomChange != 1f ||
                        panChange != Offset.Zero
                    ) {
                        onGesture(
                            centroid,
                            panChange,
                            zoomChange,
                            effectiveRotation,
                            pointer,
                            event.changes
                        )
                    }

                    if (consume) {
                        event.changes.forEach {
                            if (it.positionChanged()) {
                                it.consume()
                            }
                        }
                    }
                }
            }
        } while (!canceled && event.changes.any { it.pressed })
        onGestureEnd(pointer)
    }
}
