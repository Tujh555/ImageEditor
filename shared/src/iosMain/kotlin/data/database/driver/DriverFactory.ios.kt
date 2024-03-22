package data.database.driver

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.project.Database

actual class DatabaseFactory {
    actual fun create(): Database = Database(
        NativeSqliteDriver(
            schema = Database.Schema,
            name = DATABASE_NAME
        )
    )
}