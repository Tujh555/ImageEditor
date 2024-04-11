package api

import com.example.project.shared.ImageDatabase
import implementation.data.entity.adapter.imageEntityAdapter
import implementation.data.repository.ImageRepositoryImpl
import implementation.domain.repository.ImageRepository

interface CommonApi {
    val repository: ImageRepository
}

internal class CommonApiImpl(dependencies: PlatformDependencies) : CommonApi {
    private val database = ImageDatabase(dependencies.databaseDriver, imageEntityAdapter)
    override val repository: ImageRepository = ImageRepositoryImpl(database)
}