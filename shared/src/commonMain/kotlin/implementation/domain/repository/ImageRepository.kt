package implementation.domain.repository

import implementation.domain.models.Image
import kotlinx.coroutines.flow.StateFlow

interface ImageRepository {
    val list: StateFlow<List<Image>>

    fun insert(item: Image)

    fun delete(id: String)
}