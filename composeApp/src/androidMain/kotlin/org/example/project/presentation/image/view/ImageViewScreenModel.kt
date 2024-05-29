package org.example.project.presentation.image.view

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
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
import org.example.project.editor.transformation.Transformation
import org.example.project.editor.transformation.draw.DrawTransformation
import org.example.project.presentation.base.BaseScreenModel
import org.example.project.presentation.models.ImageUiModel
import java.util.LinkedList

internal class ImageViewScreenModel(
    image: ImageUiModel,
    private val saveBitmap: SaveBitmap,
    private val context: Context
) : BaseScreenModel<ImageViewScreen.Action, ImageViewScreen.State>(
    initialState = ImageViewScreen.State(
        image = image,
    )
) {
    private val previousStates = LinkedList<Bitmap>()
    private var previousState: EditingState? = null

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
                saveBitmap(action.bitmap)

            is ImageViewScreen.Action.UpdateBitmap ->
                updateBitmap(action.bitmap)

            is ImageViewScreen.Action.SelectTransformation ->
                selectTransformation(action.transformation)

            ImageViewScreen.Action.Undo ->
                undoOperation()

            ImageViewScreen.Action.CloseEditing -> closeEditing()

            ImageViewScreen.Action.UndoOperation -> undo()
        }
    }

    private fun closeEditing() {
        val editingState = state.value.editingState as? EditingState.TransformationSelected ?: return
        editingState.transformation.clear()

        _state.update {
            it.copy(
                editingState = previousState ?: EditingState.Initial
            )
        }
    }

    private fun updateState(newState: (ImageViewScreen.State) -> ImageViewScreen.State) {
        previousState = state.value.editingState

        _state.update(newState)
    }

    private fun undo() {
        val editingState = state.value.editingState as? EditingState.TransformationSelected ?: return
        editingState.transformation.undo()
    }

    private fun selectTransformation(transformation: Transformation) {
        updateState {
            it.copy(
                editingState = EditingState.TransformationSelected(transformation)
            )
        }
    }

    private fun saveBitmap(bitmap: Bitmap) {
        ioScope.launch {
            saveBitmap(bitmap, CompressFormat.Jpeg())

            withContext(Dispatchers.Main) {
                Toast
                    .makeText(context, "Фото сохранено", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun updateBitmap(bitmap: Bitmap) {

        previousState?.let {
            if (it is EditingState.SavedBitmap) {
                savePrevious(it.bitmap)
            }
        }

        updateState {
            it.copy(
                editingState = EditingState.SavedBitmap(bitmap.asImageBitmap())
            )
        }
    }

    private fun savePrevious(bitmap: ImageBitmap) {
        with(previousStates) {
            if (size > UNDO_LIMIT) {
                removeFirst().recycle()
            }

            addLast(bitmap.asAndroidBitmap())
        }
    }

    private fun undoOperation() {
        val currentState = state.value.editingState

        if (currentState !is EditingState.SavedBitmap) return

        previousState?.let {
            if (it is EditingState.TransformationSelected) {
                it.transformation.undo()
            }
        }

        with(previousStates) {
            if (size == 0) {
                _state.update { it.copy(editingState = EditingState.Initial) }
                return
            }

            val currentBitmap = currentState.bitmap.asAndroidBitmap()
            val lastBitmap = removeLast()
            _state.update {
                it.copy(
                    editingState = EditingState.SavedBitmap(lastBitmap.asImageBitmap())
                )
            }

            currentBitmap.recycle()
        }
    }

    companion object {
        private const val UNDO_LIMIT = 10
    }
}