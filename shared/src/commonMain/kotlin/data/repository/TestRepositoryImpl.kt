package data.repository

import data.database.DatabaseInstance
import domain.TestData
import domain.TestRepository

internal class TestRepositoryImpl : TestRepository {

    override suspend fun getAllSortedByRating() = DatabaseInstance
        .get()
        .test_tableQueries
        .entriesSortedByRating { id, title, rating ->
            TestData(id, title, rating)
        }
        .executeAsList()
}

val testRepository: TestRepository = TestRepositoryImpl()