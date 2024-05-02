package org.example.project.presentation.image.view

import org.example.project.domain.compressor.CompressFormat
import org.example.project.domain.uc.SaveBitmap
import org.example.project.presentation.base.BaseScreenModel
import org.example.project.presentation.models.ImageUiModel

internal class ImageViewScreenModel(
    image: ImageUiModel,
    private val saveBitmap: SaveBitmap
) : BaseScreenModel<ImageViewScreen.Action, ImageViewScreen.State>(
    initialState = ImageViewScreen.State(
        image = image,
        transformations = listOf()
    )
) {
    override fun onAction(action: ImageViewScreen.Action) {
        when (action) {
            is ImageViewScreen.Action.Save -> saveBitmap(action.bitmap, CompressFormat.Jpeg())
        }
    }
}