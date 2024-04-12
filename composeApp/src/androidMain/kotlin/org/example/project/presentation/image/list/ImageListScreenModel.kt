package org.example.project.presentation.image.list

import android.util.Log
import implementation.domain.models.Image
import implementation.domain.repository.ImageRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.example.project.presentation.base.BaseScreenModel

internal class ImageListScreenModel(
    private val repository: ImageRepository
) : BaseScreenModel<ImageListScreen.Action, ImageListScreen.State>(
    initialState = ImageListScreen.State(emptyList())
) {
    init {
        observeImageList()

        ioScope.launch {
            repeat(3) {
                delay(500)
                repository.insert(
                    Image(
                        id = "$it",
                        name = "Name $it",
                        saveDate = Clock.System.now(),
                        path = "empty path"
                    )
                )
            }
        }
    }

    override fun onAction(action: ImageListScreen.Action) {
        TODO("Not yet implemented")
    }

    private fun observeImageList() {
        repository
            .list
            .onEach { images ->
                _state.update {
                    it.copy(imagesList = images.toList())
                }
            }
            .launchIn(ioScope)
    }
}