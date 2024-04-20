package org.example.project.presentation.image.view

import org.example.project.presentation.base.BaseScreenModel
import org.example.project.presentation.models.ImageUiModel

internal class ImageViewScreenModel(
    image: ImageUiModel
) : BaseScreenModel<ImageViewScreen.Action, ImageViewScreen.State>(
    initialState = ImageViewScreen.State(image = image)
) {
    override fun onAction(action: ImageViewScreen.Action) {
        TODO("Not yet implemented")
    }
}