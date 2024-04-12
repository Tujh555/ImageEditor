package api

import app.cash.sqldelight.db.SqlDriver

expect class PlatformDependencies {
    val databaseDriver: SqlDriver
}

fun interface DependenciesFactory {
    fun create(): PlatformDependencies
}

