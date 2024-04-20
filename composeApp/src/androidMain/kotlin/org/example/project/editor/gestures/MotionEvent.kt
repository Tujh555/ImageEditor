package org.example.project.editor.gestures

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset

@Immutable
internal sealed interface MotionEvent {
    val position: Offset

    val x: Float
        get() = position.x
    val y: Float
        get() = position.y

    data object Unspecified : MotionEvent {
        override val position: Offset = Offset.Unspecified
    }

    @JvmInline
    value class Down(override val position: Offset) : MotionEvent

    @JvmInline
    value class Move(override val position: Offset) : MotionEvent

    @JvmInline
    value class Up(override val position: Offset) : MotionEvent
}