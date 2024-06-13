package org.example.project.presentation.image.view

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.editor.transformation.Transformation
import org.example.project.editor.transformation.TransformationFactory
import org.example.project.editor.transformation.crop.CropTransformation
import org.example.project.editor.transformation.crop.CropTransformationFactory
import org.example.project.editor.transformation.draw.DrawTransformationFactory
import org.example.project.presentation.base.BaseScreen
import org.example.project.presentation.models.ImageUiModel
import org.example.project.utils.NeverEquals
import org.koin.core.parameter.parametersOf

internal class ImageViewScreen(
    private val image: ImageUiModel
) : BaseScreen<ImageViewScreen.Action, ImageViewScreen.State, ImageViewScreen.Event, ImageViewScreenModel>() {

    @Immutable
    data class State(
        val imageName: String,
        val bitmap: ImageBitmap,
        val transformationFactories: List<TransformationFactory> = listOf(
            DrawTransformationFactory(),
            CropTransformationFactory()
        ),
        val selectedTransformation: Transformation? = null,
        val haveStory: Boolean = false
    )

    @Immutable
    sealed interface Action {
        data object SaveToStorage : Action
        data class SaveTransformationResult(val bitmap: Bitmap) : Action
        data class SelectTransformation(val transformationFactory: TransformationFactory) : Action
        data object Undo : Action
        data object CloseEditing : Action
    }
    
    @Immutable
    sealed interface Event {
        class CloseScreen : Event, NeverEquals()
    }

    @Composable
    override fun Content(state: State, onAction: (Action) -> Unit) {
        ImageViewScreenContent(state, onAction)
    }

    @Composable
    override fun Event(event: Event) {
        val navigator = LocalNavigator.currentOrThrow
        
        LaunchedEffect(event) {
            when (event) {
                is Event.CloseScreen -> navigator.pop()
            }
        }
    }

    @Composable
    override fun getModel(): ImageViewScreenModel {
        return getScreenModel {
            parametersOf(image)
        }
    }
}