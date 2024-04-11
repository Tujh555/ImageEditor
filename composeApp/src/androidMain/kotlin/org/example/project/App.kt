package org.example.project

import android.app.Application
import api.CommonApiHolder
import api.PlatformDependencies

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        CommonApiHolder.init {
            PlatformDependencies(applicationContext)
        }
    }
}