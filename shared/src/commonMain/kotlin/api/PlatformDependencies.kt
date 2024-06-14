package api

import app.cash.sqldelight.db.SqlDriver

expect class PlatformDependencies {
    val databaseDriver: SqlDriver
}

