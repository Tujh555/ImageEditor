package org.example.project.di.modules

import api.CommonApi
import org.example.project.presentation.image.list.ImageListScreenModel
import org.koin.dsl.module

internal val presentationModule = module {
    factory { ImageListScreenModel(get<CommonApi>().repository) }
}