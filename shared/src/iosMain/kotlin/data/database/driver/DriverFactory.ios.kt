package data.database.driver

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.project.Database

class DatabaseFactoryIos : DatabaseFactory {
    override fun create(): Database = Database(
        NativeSqliteDriver(
            schema = Database.Schema,
            name = DATABASE_NAME
        )
    )
}