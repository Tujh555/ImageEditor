package org.example.project.presentation.image.view

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.project.domain.compressor.CompressFormat
import org.example.project.domain.uc.SaveBitmap
import org.example.project.editor.transformation.TransformationFactory
import org.example.project.presentation.base.BaseScreenModel
import org.example.project.presentation.models.ImageUiModel
import org.example.project.utils.BitmapStorage

internal class ImageViewScreenModel(
    image: ImageUiModel,
    private val saveBitmap: SaveBitmap,
    private val context: Context,
    private val bitmapStorage: BitmapStorage
) : BaseScreenModel<ImageViewScreen.Action, ImageViewScreen.State, ImageViewScreen.Event>(
    initialState = ImageViewScreen.State(
        imageName = image.name,
        bitmap = BitmapFactory
            .decodeFile(image.path.path)
            .asImageBitmap()
    )
) {
    private val currentBitmap
        get() = state.value.bitmap

    override fun onAction(action: ImageViewScreen.Action) {
        when (action) {
            is ImageViewScreen.Action.SaveToStorage ->
                saveBitmap()

            is ImageViewScreen.Action.SaveTransformationResult ->
                updateBitmap(action.bitmap.asImageBitmap())

            is ImageViewScreen.Action.SelectTransformation ->
                selectTransformation(action.transformationFactory)

            ImageViewScreen.Action.Undo ->
                undo()

            ImageViewScreen.Action.CloseEditing ->
                closeEditing()
        }
    }

    private fun closeEditing() {
        _state.update {
            it.copy(
                selectedTransformation = null
            )
        }
    }

    private fun undo() {
        val selectedTransformation = state.value.selectedTransformation

        if (selectedTransformation != null) {
            selectedTransformation.undo()
            return
        }

        val previousImage = bitmapStorage.pop()?.asImageBitmap()

        if (previousImage != null) {
            _state.update {
                it.copy(
                    bitmap = previousImage
                )
            }
        }

        val haveCache = bitmapStorage.cacheSize > 0
        _state.update {
            it.copy(
                haveStory = haveCache
            )
        }
    }

    private fun selectTransformation(factory: TransformationFactory) {
        val selectedTransformation = factory.createWithBitmap(currentBitmap)

        _state.update {
            it.copy(
                selectedTransformation = selectedTransformation
            )
        }
    }

    private fun updateBitmap(newBitmap: ImageBitmap) {
        bitmapStorage.put(currentBitmap.asAndroidBitmap())

        _state.update {
            it.copy(
                bitmap = newBitmap,
                haveStory = bitmapStorage.cacheSize > 0
            )
        }

        closeEditing()
    }

    private fun saveBitmap() {
        ioScope.launch {
            saveBitmap(currentBitmap.asAndroidBitmap(), CompressFormat.Jpeg())

            withContext(Dispatchers.Main) {
                Toast
                    .makeText(context, "Фото сохранено", Toast.LENGTH_SHORT)
                    .show()
            }

            _event.send(ImageViewScreen.Event.CloseScreen())
        }
    }

    override fun onDispose() {
        bitmapStorage.clear()
    }
}