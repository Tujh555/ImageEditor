package api

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.project.shared.ImageDatabase
import implementation.data.DATABASE_NAME

actual class PlatformDependencies(context: Context) {
    actual val databaseDriver: SqlDriver = AndroidSqliteDriver(
        schema = ImageDatabase.Schema,
        context = context,
        name = DATABASE_NAME
    )
}