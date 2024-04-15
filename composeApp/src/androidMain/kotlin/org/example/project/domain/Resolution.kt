package org.example.project.domain

import kotlin.math.max

data class Resolution(val width: Int, val height: Int) {
    val maxSide = max(width, height)

    companion object {
        val ZERO = Resolution(width = 0, height = 0)
        val FULL_HD = Resolution(width = 1920, height = 1080)
    }
}
