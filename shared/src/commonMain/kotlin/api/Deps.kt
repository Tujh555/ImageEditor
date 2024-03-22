package api

import data.database.driver.DatabaseFactory
import data.repository.TestRepositoryImpl
import domain.TestRepository

object Deps {
    fun get(factory: DatabaseFactory): TestRepository {
        return TestRepositoryImpl(factory)
    }
}