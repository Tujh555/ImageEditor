package org.example.project.presentation.screen.navigation.router

import org.example.project.presentation.screen.navigation.ScreenTransformation

interface TransformationSupplier {
    fun transform(transformation: ScreenTransformation)
}