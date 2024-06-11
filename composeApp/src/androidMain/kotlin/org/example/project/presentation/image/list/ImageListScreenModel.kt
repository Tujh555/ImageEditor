package org.example.project.presentation.image.list

import android.content.Context
import android.net.Uri
import android.widget.Toast
import implementation.domain.models.Resource
import implementation.domain.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.project.domain.uc.CompressImage
import org.example.project.domain.uc.DeleteImage
import org.example.project.presentation.base.BaseScreenModel
import org.example.project.presentation.mapper.ImageListFormatter
import org.example.project.presentation.models.ImageUiModel

internal class ImageListScreenModel(
    private val repository: ImageRepository,
    private val formatter: ImageListFormatter,
    private val compressImage: CompressImage,
    private val context: Context,
    private val deleteImage: DeleteImage
) : BaseScreenModel<ImageListScreen.Action, ImageListScreen.State, Nothing>(
    initialState = ImageListScreen.State()
) {
    init {
        observeImageList()
    }

    override fun onAction(action: ImageListScreen.Action) {
        when (action) {
            is ImageListScreen.Action.SaveImage -> save(action.uri)
            is ImageListScreen.Action.DeleteImage -> delete(action.image)
        }
    }

    private fun delete(image: ImageUiModel) {
        ioScope.launch {
            val result = deleteImage(
                id = image.id,
                uri = image.path
            )

            if (result.isFailure) {
                withContext(Dispatchers.Main) {
                    Toast
                        .makeText(context, "Ошибка удаления", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun save(uri: Uri) {
        ioScope.launch {
            val toastText = when (compressImage(uri)) {
                is Resource.Failure -> "Ошибка"
                is Resource.Success -> "Сохранено"
            }

            withContext(Dispatchers.Main) {
                Toast
                    .makeText(context, toastText, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun observeImageList() {
        _state.update {
            it.copy(
                isLoading = true
            )
        }

        repository
            .list
            .onEach { images ->
                if (state.value.isLoading) {
                    delay(1000)
                }

                val imageMap = formatter.format(images)

                _state.update {
                    it.copy(
                        imagesDateMap = imageMap,
                        isLoading = false
                    )
                }
            }
            .launchIn(ioScope)
    }
}