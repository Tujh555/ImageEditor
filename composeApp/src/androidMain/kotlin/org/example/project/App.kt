package org.example.project

import android.app.Application
import org.example.project.di.modules.commonModule
import org.example.project.di.modules.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

internal class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            modules(commonModule, presentationModule)
        }
    }
}