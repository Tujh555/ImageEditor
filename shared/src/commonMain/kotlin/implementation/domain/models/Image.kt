package implementation.domain.models

import kotlinx.datetime.Instant

data class Image(
    val id: String,
    val name: String,
    val saveDate: Instant,
    val path: String
)
