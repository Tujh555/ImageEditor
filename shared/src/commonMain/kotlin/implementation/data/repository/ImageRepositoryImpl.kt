package implementation.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.project.shared.ImageDatabase
import comexampleprojectshareddatabase.ImageEntity
import implementation.coroutines.scope.SingletonScope
import implementation.data.mapper.toDb
import implementation.data.mapper.toDomain
import implementation.domain.models.Image
import implementation.domain.repository.ImageRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class ImageRepositoryImpl(database: ImageDatabase) : ImageRepository {
    private val ioScope = SingletonScope.IO.value
    private val queries = database.image_databaseQueries

    override val list: StateFlow<List<Image>> = queries
        .selectAll()
        .asFlow()
        .mapToList(ioScope.coroutineContext)
        .map { it.map(ImageEntity::toDomain) }
        .stateIn(
            scope = ioScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    override fun insert(item: Image) {
        queries.insert(item.toDb())
    }

    override fun delete(id: String) {
        queries.remove(id)
    }
}