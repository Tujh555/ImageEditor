package org.example.project.editor.transformation.crop

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerInputChange

@Stable
internal class GridState(initialRect: Rect) {
    var rect by mutableStateOf(initialRect)
        private set

    fun onDragStart(offset: Offset) {

    }

    fun onDragEnd() {

    }

    fun onDrag(change: PointerInputChange, dragAmount: Offset) {

    }
}

internal enum class TouchArea {
    Flank, Corner
}