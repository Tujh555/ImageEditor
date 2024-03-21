package data.database.driver

import App
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.project.Database

actual class DriverFactory actual constructor(){
    actual fun create(): SqlDriver = AndroidSqliteDriver(
        schema = Database.Schema,
        context = App.context,
        name = DATABASE_NAME
    )
}