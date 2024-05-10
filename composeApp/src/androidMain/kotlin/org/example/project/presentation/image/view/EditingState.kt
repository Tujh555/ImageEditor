package org.example.project.presentation.image.view

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import org.example.project.editor.transformation.Transformation

@Immutable
internal sealed interface EditingState {
    data object Initial : EditingState

    data class SavedBitmap(val bitmap: ImageBitmap) : EditingState

    data class TransformationSelected(val transformation: Transformation) : EditingState
}