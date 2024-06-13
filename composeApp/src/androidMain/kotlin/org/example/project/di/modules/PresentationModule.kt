package org.example.project.di.modules

import org.example.project.domain.uc.DeleteImage
import org.example.project.presentation.image.list.ImageListScreenModel
import org.example.project.utils.BitmapStorage
import org.example.project.presentation.image.view.ImageViewScreenModel
import org.example.project.presentation.mapper.ImageListFormatter
import org.koin.dsl.module

internal val presentationModule = module {
    factory {
        ImageListScreenModel(
            repository = get(),
            formatter = get(),
            compressImage = get(),
            context = get(),
            deleteImage = get()
        )
    }

    factory { BitmapStorage() }

    factory {
        ImageViewScreenModel(get(), get(), get(), get())
    }

    single { ImageListFormatter(get()) }

    factory { DeleteImage(get(), get()) }
}