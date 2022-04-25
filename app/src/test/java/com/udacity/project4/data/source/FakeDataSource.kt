package com.udacity.project4.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.project4.data.model.Reminder
import com.udacity.project4.data.repo.IDataSource

class FakeDataSource(private var reminders:MutableList<Reminder>? = mutableListOf()): IDataSource {

    private val reminderListLiveData = MutableLiveData<List<Reminder>>(reminders)

    override suspend fun getReminders(): List<Reminder>{
        return reminders!!
    }

    override suspend fun getReminder(title: String): Reminder {
        return reminders?.find { it.title == title } ?: Reminder(1,"","","","","")
    }

    override suspend fun refreshReminder(): List<Reminder> {
        return reminders!!
    }

    override suspend fun saveReminder(reminder: Reminder): Long{
        reminders?.add(reminder)
        val reminderToReturn = reminders?.find { it.title == reminder.title }!!
        return reminderToReturn.id
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        reminders?.remove(reminder)
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }

    override fun getAllReminder(): LiveData<List<Reminder>> {
        return reminderListLiveData
    }
    private fun refreshData(){
        reminderListLiveData.postValue(reminders)
    }
}