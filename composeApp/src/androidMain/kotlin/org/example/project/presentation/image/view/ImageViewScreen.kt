package org.example.project.presentation.image.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.koin.getScreenModel
import org.example.project.editor.ImageEditor
import org.example.project.presentation.base.BaseScreen
import org.example.project.presentation.models.ImageUiModel
import org.koin.core.parameter.parametersOf

internal class ImageViewScreen(
    private val image: ImageUiModel
) : BaseScreen<ImageViewScreen.Action, ImageViewScreen.State, ImageViewScreenModel>() {

    @Immutable
    data class State(
        val image: ImageUiModel,
        val imageEditor: ImageEditor
    )

    @Immutable
    sealed interface Action {
        data object Save : Action
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