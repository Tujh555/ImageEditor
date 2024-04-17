package org.example.project.presentation.image.list

import implementation.domain.repository.ImageRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.example.project.presentation.base.BaseScreenModel
import org.example.project.presentation.mapper.ImageListFormatter

private val urls = listOf(
    "https://freshfeeds.in/img_urls/img4.jpg",
    "https://freshfeeds.in/img_urls/img5.avaif",
    "https://freshfeeds.in/img_urls/img6.png",
    "https://freshfeeds.in/img_urls/img7.png",
    "https://freshfeeds.in/img_urls/img8.jpg",
    "https://freshfeeds.in/img_urls/img9.jpg",
    "https://freshfeeds.in/img_urls/img10.webp",
    "https://freshfeeds.in/img_urls/img11.jpg",
    "https://freshfeeds.in/img_urls/img12.webp",
    "https://freshfeeds.in/img_urls/img13.png",
    "https://freshfeeds.in/img_urls/img14.jpg",
    "https://freshfeeds.in/img_urls/img15.jpg",
    "https://freshfeeds.in/img_urls/img19.png",
    "https://freshfeeds.in/img_urls/img20.png",
    "https://freshfeeds.in/img_urls/img21.png",
    "https://freshfeeds.in/img_urls/img22.png",
    "https://freshfeeds.in/img_urls/img23.png",
    "https://freshfeeds.in/img_urls/img24.jpg",
    "https://freshfeeds.in/img_urls/img25.png",
    "https://freshfeeds.in/img_urls/img1.jpg",
    "https://freshfeeds.in/img_urls/img2.png",
)

internal class ImageListScreenModel(
    private val repository: ImageRepository,
    private val formatter: ImageListFormatter
) : BaseScreenModel<ImageListScreen.Action, ImageListScreen.State>(
    initialState = ImageListScreen.State()
) {
    init {
        observeImageList()
    }

    override fun onAction(action: ImageListScreen.Action) {

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