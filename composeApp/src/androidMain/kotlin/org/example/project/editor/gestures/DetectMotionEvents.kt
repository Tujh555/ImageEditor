package org.example.project.editor.gestures

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastFirstOrNull
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

@Stable
internal fun Modifier.onOneTouchEvents(
    key: Any? = null,
    downDelay: Long = 25L,
    onEvent: (MotionEvent) -> Unit
) = this.pointerInput(key) {
    detectOneTouchEvents(
        downDelay = downDelay,
        onDown = {
            onEvent(MotionEvent.Down(it.position))
            it.consume()
        },
        onUp = {
            onEvent(MotionEvent.Up(it.position))
            it.consume()
        },
        onMove = {
            onEvent(MotionEvent.Move(it.position))
            it.consume()
        }
    )
}

internal suspend fun PointerInputScope.detectOneTouchEvents(
    downDelay: Long = 25L,
    onDown: (PointerInputChange) -> Unit = {},
    onUp: (PointerInputChange) -> Unit = {},
    onMove: (PointerInputChange) -> Unit = {},
) {
    coroutineScope {
        awaitEachGesture {
            val down = awaitFirstDown().also(onDown)

            var currentPointer = down
            val isWaitAfterDown = AtomicBoolean(false)

            launch {
                delay(downDelay)
                isWaitAfterDown.set(true)
            }

            while (isActive) {
                val event = awaitPointerEvent()
                val isAnyPressed = event.changes.fastAny { it.pressed }

                if (isAnyPressed.not()) {
                    onUp(currentPointer)
                    break
                }

                event.changes
                    .fastFirstOrNull { it.id == currentPointer.id }
                    ?.let { change ->
                        currentPointer = change

                        if (isWaitAfterDown.get()) {
                            onMove(change)
                        }
                    }
            }
        }
    }
}