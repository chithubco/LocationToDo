package com.udacity.project4

import android.app.Application
import com.udacity.project4.data.repo.IReminderRepo
import com.udacity.project4.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ToDoApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ToDoApplication)
            modules(listOf(appModule))
        }
    }
    val reminderRepo: IReminderRepo
    get() = ServiceLocator.provideTasksRepository(this)
}