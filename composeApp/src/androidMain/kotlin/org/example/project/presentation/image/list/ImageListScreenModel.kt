package org.example.project.presentation.image.list

import android.net.Uri
import implementation.domain.models.onError
import implementation.domain.models.onSuccess
import implementation.domain.repository.ImageRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.example.project.domain.uc.SaveImage
import org.example.project.presentation.base.BaseScreenModel
import org.example.project.presentation.mapper.ImageListFormatter

internal class ImageListScreenModel(
    private val repository: ImageRepository,
    private val formatter: ImageListFormatter,
    private val saveImage: SaveImage
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
        saveImage(uri)
            .onSuccess {
                // TODO snack
            }
            .onError {
                // TODO
            }
    }

    private fun observeImageList() {
        repository
            .list
            .onEach { images ->
                val imageMap = formatter.format(images)

                _state.update {
                    it.copy(
                        imagesDateMap = imageMap
                    )
                }
            }
            .launchIn(ioScope)
    }
}