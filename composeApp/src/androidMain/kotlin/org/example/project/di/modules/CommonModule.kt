package org.example.project.di.modules

import api.CommonApi
import api.PlatformDependencies
import org.koin.dsl.module

internal val commonModule = module {
    single { PlatformDependencies(get()) }

    single { CommonApi(get()) }
}