package com.udacity.project4

import android.app.Application
import com.udacity.project4.data.repo.IReminderRepo

class ToDoApplication:Application() {

    val reminderRepo: IReminderRepo
    get() = ServiceLocator.provideTasksRepository(this)
}