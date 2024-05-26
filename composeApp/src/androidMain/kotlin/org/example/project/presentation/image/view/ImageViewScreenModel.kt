package org.example.project.presentation.image.view

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.project.domain.compressor.CompressFormat
import org.example.project.domain.uc.SaveBitmap
import org.example.project.editor.transformation.draw.DrawTransformation
import org.example.project.presentation.base.BaseScreenModel
import org.example.project.presentation.models.ImageUiModel

internal class ImageViewScreenModel(
    image: ImageUiModel,
    private val saveBitmap: SaveBitmap,
    private val context: Context
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
            is ImageViewScreen.Action.Save -> {
                ioScope.launch {
                    saveBitmap(action.bitmap, CompressFormat.Jpeg())

                    withContext(Dispatchers.Main) {
                        Toast
                            .makeText(context, "Image was saved", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

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
        }
    }
}