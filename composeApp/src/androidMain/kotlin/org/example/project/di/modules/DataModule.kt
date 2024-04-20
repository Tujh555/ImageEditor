package org.example.project.di.modules

import org.example.project.data.ImageCompressorImpl
import org.example.project.data.MetadataLoaderImpl
import org.example.project.domain.compressor.ImageCompressor
import org.example.project.domain.metadata.MetadataLoader
import org.example.project.domain.uc.CompressImage
import org.example.project.domain.uc.SaveBitmap
import org.koin.dsl.module

internal val dataModule = module {
    factory<MetadataLoader> { MetadataLoaderImpl(get()) }

    factory<ImageCompressor> { ImageCompressorImpl(get()) }

    factory { CompressImage(get(), get(), get()) }

    factory { SaveBitmap(get(), get()) }
}