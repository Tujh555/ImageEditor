package org.example.project.presentation.mapper

import api.time.DateFormatter
import implementation.domain.models.Image
import org.example.project.presentation.models.ImageUiModel
import org.example.project.presentation.models.toUi

internal class ImageListFormatter(private val dateFormatter: DateFormatter) {
    fun format(images: List<Image>): Map<String, List<ImageUiModel>> {
        return images.groupBy(
            keySelector = {
                dateFormatter.format(it.saveDate)
            },
            valueTransform = {
                it.toUi(dateFormatter::format)
            }
        )
    }
}