package org.example.project.presentation.image.list

import android.content.SharedPreferences
import android.net.Uri
import implementation.domain.models.onError
import implementation.domain.models.onSuccess
import implementation.domain.repository.ImageRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.uc.CompressImage
import org.example.project.presentation.base.BaseScreenModel
import org.example.project.presentation.mapper.ImageListFormatter

internal class ImageListScreenModel(
    private val repository: ImageRepository,
    private val formatter: ImageListFormatter,
    private val compressImage: CompressImage
) : BaseScreenModel<ImageListScreen.Action, ImageListScreen.State>(
    initialState = ImageListScreen.State()
) {
    init {
        observeImageList()
    }

    override fun onAction(action: ImageListScreen.Action) {
        when (action) {
            is ImageListScreen.Action.SaveImage -> save(action.uri)
        }
    }

    private fun save(uri: Uri) {
        ioScope.launch {
            compressImage(uri)
                .onSuccess {
                    // TODO snack
                }
                .onError {
                    // TODO
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