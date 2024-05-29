package org.example.project.presentation.image.view

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.koin.getScreenModel
import org.example.project.editor.transformation.Transformation
import org.example.project.presentation.base.BaseScreen
import org.example.project.presentation.models.ImageUiModel
import org.koin.core.parameter.parametersOf

internal class ImageViewScreen(
    private val image: ImageUiModel
) : BaseScreen<ImageViewScreen.Action, ImageViewScreen.State, ImageViewScreenModel>() {

    @Immutable
    data class State(
        val image: ImageUiModel,
        val transformations: List<Transformation> = emptyList(),
        val editingState: EditingState = EditingState.Initial
    )

    @Immutable
    sealed interface Action {
        data class Save(val bitmap: Bitmap) : Action
        data class UpdateBitmap(val bitmap: Bitmap) : Action
        data class SelectTransformation(val transformation: Transformation) : Action
        data object Undo : Action
        data object CloseEditing : Action
        data object UndoOperation : Action
    }

    @Composable
    override fun Content(state: State, onAction: (Action) -> Unit) {
        ImageViewScreenContent(state, onAction)
    }

    @Composable
    override fun getModel(): ImageViewScreenModel {
        return getScreenModel {
            parametersOf(image)
        }
    }
}