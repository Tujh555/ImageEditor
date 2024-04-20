package org.example.project.presentation.image.view

import kotlinx.coroutines.launch
import org.example.project.domain.compressor.CompressFormat
import org.example.project.domain.uc.SaveBitmap
import org.example.project.editor.ImageEditor
import org.example.project.presentation.base.BaseScreenModel
import org.example.project.presentation.models.ImageUiModel

internal class ImageViewScreenModel(
    image: ImageUiModel,
    private val saveBitmap: SaveBitmap
) : BaseScreenModel<ImageViewScreen.Action, ImageViewScreen.State>(
    initialState = ImageViewScreen.State(
        imageEditor = ImageEditor(image.path),
        image = image
    )
) {
    override fun onAction(action: ImageViewScreen.Action) {
        when (action) {
            ImageViewScreen.Action.Save -> ioScope.launch {
                saveBitmap(
                    bitmap = state.value.imageEditor.bitmap.value,
                    format = CompressFormat.Jpeg()
                )
            }
        }
    }
}