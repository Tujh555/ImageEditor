package api

import api.time.DateFormatter
import com.example.project.shared.ImageDatabase
import implementation.data.entity.adapter.imageEntityAdapter
import implementation.data.repository.ImageRepositoryImpl
import implementation.domain.repository.ImageRepository
import implementation.time.DateFormatterImpl

interface CommonApi {
    val repository: ImageRepository
    val timeFormatter: DateFormatter
}

fun CommonApi(dependencies: PlatformDependencies): CommonApi = CommonApiImpl(dependencies)

internal class CommonApiImpl(dependencies: PlatformDependencies) : CommonApi {
    private val database = ImageDatabase(dependencies.databaseDriver, imageEntityAdapter)

    override val repository: ImageRepository = ImageRepositoryImpl(database)

    override val timeFormatter: DateFormatter = DateFormatterImpl()
}