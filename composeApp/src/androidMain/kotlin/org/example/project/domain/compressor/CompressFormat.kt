package org.example.project.domain.compressor

sealed interface CompressFormat {
    val quantity: Int

    data class Jpeg(override val quantity: Int = HIGH_QUANTITY) : CompressFormat

    data class Png(override val quantity: Int = HIGH_QUANTITY) : CompressFormat

    data class Webp(override val quantity: Int = HIGH_QUANTITY) : CompressFormat

    companion object {
        const val LOW_QUANTITY = 20
        const val MEDIUM_QUANTITY = 50
        const val HIGH_QUANTITY = 80
        const val FULL_QUANTITY = 100
    }
}