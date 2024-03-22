package data.repository

import data.database.driver.DatabaseFactory
import domain.TestData
import domain.TestRepository

internal class TestRepositoryImpl(private val factory: DatabaseFactory) : TestRepository {

    override suspend fun getAllSortedByRating() = factory
        .create()
        .test_tableQueries
        .entriesSortedByRating { id, title, rating ->
            TestData(id, title, rating)
        }
        .executeAsList()
}