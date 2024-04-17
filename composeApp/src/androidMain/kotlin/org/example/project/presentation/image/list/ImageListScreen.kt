package org.example.project.presentation.image.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.koin.getScreenModel
import org.example.project.presentation.base.BaseScreen
import org.example.project.presentation.models.ImageUiModel

internal class ImageListScreen :
    BaseScreen<ImageListScreen.Action, ImageListScreen.State, ImageListScreenModel>() {

    @Immutable
    data class State(val imagesDateMap: Map<String, List<ImageUiModel>> = emptyMap())

    @Immutable
    sealed interface Action

    @Composable
    override fun Content(state: State, onAction: (Action) -> Unit) {
        ImageListScreenContent(state, onAction)
    }

    @Composable
    override fun getModel(): ImageListScreenModel {
        return getScreenModel<ImageListScreenModel>()
    }
}