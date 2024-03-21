package domain

interface TestRepository {
    suspend fun getAllSortedByRating(): List<TestData>
}