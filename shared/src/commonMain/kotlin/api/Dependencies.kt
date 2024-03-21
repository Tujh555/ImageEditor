package api

import data.database.driver.DatabaseFactory
import data.repository.TestRepositoryImpl
import domain.TestRepository

interface Dependencies {
    val databaseFactory: DatabaseFactory
}

interface CommonApi {
    val repository: TestRepository
}

object CommonHolder {
    fun get(dependencies: Dependencies): CommonApi {
        return object : CommonApi {
            override val repository: TestRepository = TestRepositoryImpl(dependencies.databaseFactory)
        }
    }
}