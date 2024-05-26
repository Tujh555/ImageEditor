package org.example.project.di.modules

import org.example.project.domain.compressor.ImageCompressor
import org.koin.dsl.module
import org.example.project.data.ImageCompressorImpl

internal val compressorModule = module {
    single<ImageCompressor> { ImageCompressorImpl(get()) }
}