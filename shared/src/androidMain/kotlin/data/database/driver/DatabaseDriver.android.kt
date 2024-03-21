package data.database.driver

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.project.Database

class DatabaseFactoryAndroid(private val context: Context) : DatabaseFactory {
    override fun create(): Database = Database(
        AndroidSqliteDriver(
            schema = Database.Schema,
            context = context,
            name = DATABASE_NAME
        )
    )
}