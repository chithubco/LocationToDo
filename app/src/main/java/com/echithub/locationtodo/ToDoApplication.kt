package com.echithub.locationtodo

import android.app.Application
import com.echithub.locationtodo.data.repo.IReminderRepo

class ToDoApplication:Application() {

    val reminderRepo: IReminderRepo
    get() = ServiceLocator.provideTasksRepository(this)
}