package data.database.driver

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory() {
    fun create(): SqlDriver
}

const val DATABASE_NAME = "test.db"