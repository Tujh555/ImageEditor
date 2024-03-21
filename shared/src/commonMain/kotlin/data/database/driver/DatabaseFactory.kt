package data.database.driver

import app.cash.sqldelight.db.SqlDriver
import com.example.project.Database

interface DatabaseFactory {
    fun create(): Database
}

const val DATABASE_NAME = "test.db"