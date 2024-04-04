package org.example.project.presentation.screen.navigation.router

import org.example.project.presentation.screen.navigation.screen.Screen

object GlobalRouter {
    private var applier: TransformationSupplier? = null

    fun init(transformationSupplier: TransformationSupplier) {
        applier = transformationSupplier
    }

    fun push(screen: Screen) {
        applier?.transform { it + screen }
    }

    fun pop() {
        applier?.transform {
            if (it.size > 1) {
                it.dropLast(1)
            } else {
                it
            }
        }
    }

    fun replace(screen: Screen) {
        applier?.transform { it.dropLast(1) + screen }
    }
}