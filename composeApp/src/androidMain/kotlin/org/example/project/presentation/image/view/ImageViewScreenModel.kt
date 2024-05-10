package org.example.project.presentation.image.view

import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.flow.update
import org.example.project.domain.compressor.CompressFormat
import org.example.project.domain.uc.SaveBitmap
import org.example.project.editor.transformation.draw.DrawTransformation
import org.example.project.presentation.base.BaseScreenModel
import org.example.project.presentation.models.ImageUiModel

internal class ImageViewScreenModel(
    image: ImageUiModel,
    private val saveBitmap: SaveBitmap
) : BaseScreenModel<ImageViewScreen.Action, ImageViewScreen.State>(
    initialState = ImageViewScreen.State(
        image = image,
    )
) {
    init {
        _state.update {
            it.copy(
                transformations = listOf(DrawTransformation(image.path))
            )
        }
    }

    override fun onAction(action: ImageViewScreen.Action) {
        when (action) {
            is ImageViewScreen.Action.Save ->
                saveBitmap(action.bitmap, CompressFormat.Jpeg())

            is ImageViewScreen.Action.UpdateBitmap ->
                _state.update {
                    it.copy(
                        editingState = EditingState.SavedBitmap(action.bitmap.asImageBitmap())
                    )
                }

            is ImageViewScreen.Action.SelectTransformation ->
                _state.update {
                    it.copy(
                        editingState = EditingState.TransformationSelected(action.transformation)
                    )
                }

            ImageViewScreen.Action.DropTransformation -> {
                val currentEditingState = state.value.editingState


            }
        }
    }
}