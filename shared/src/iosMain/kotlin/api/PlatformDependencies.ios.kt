package api

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.project.shared.ImageDatabase
import implementation.data.DATABASE_NAME

actual class PlatformDependencies {
    actual val databaseDriver: SqlDriver = NativeSqliteDriver(
        schema = ImageDatabase.Schema,
        name = DATABASE_NAME
    )
}