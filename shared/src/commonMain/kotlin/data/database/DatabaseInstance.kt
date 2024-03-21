package data.database

import com.example.project.Database
import data.database.driver.DriverFactory
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal data object DatabaseInstance {
    private val mutex = Mutex()
    private var _instance: Database? = null

    suspend fun get(): Database {
        if (_instance != null) {
            return _instance!!
        }

        mutex.withLock {
            if (_instance != null) {
                return _instance!!
            }

            return Database(DriverFactory().create()).also { _instance = it }
        }
    }
}