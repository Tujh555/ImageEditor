package org.example.project.di.modules

import org.example.project.presentation.image.list.ImageListScreenModel
import org.example.project.presentation.mapper.ImageListFormatter
import org.koin.dsl.module

internal val presentationModule = module {
    factory {
        ImageListScreenModel(
            repository = get(),
            formatter = get(),
            saveImage = get()
        )
    }

    single { ImageListFormatter(get()) }
}