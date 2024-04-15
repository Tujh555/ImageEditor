package org.example.project.di.modules

import org.example.project.domain.compressor.ImageCompressor
import org.koin.dsl.module
import tech.inno.dion.chat.image.editor.compressor.ImageCompressorImpl

internal val compressorModule = module {
    single<ImageCompressor> { ImageCompressorImpl(get()) }
}