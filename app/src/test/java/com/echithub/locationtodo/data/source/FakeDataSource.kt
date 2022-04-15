package com.echithub.locationtodo.data.source

import androidx.lifecycle.LiveData
import com.echithub.locationtodo.data.model.Reminder
import com.echithub.locationtodo.data.repo.IDataSource

class FakeDataSource(var reminders:MutableList<Reminder>? = mutableListOf()): IDataSource {

    lateinit var reminderListLiveData: LiveData<List<Reminder>>
    override suspend fun getReminders(): List<Reminder>{
        return reminders!!
    }

    override suspend fun getReminder(title: String): Reminder {
        TODO("Not yet implemented")
    }

    override suspend fun refreshReminder() {
        TODO("Not yet implemented")
    }

    override suspend fun saveReminder(vararg reminder: Reminder){
        reminders?.addAll(reminder)
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        reminders?.remove(reminder)
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }
}