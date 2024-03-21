package data.database.driver

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.project.Database

actual class DriverFactory actual constructor(){
    actual fun create(): SqlDriver = NativeSqliteDriver(
        schema = Database.Schema,
        name = DATABASE_NAME
    )
}