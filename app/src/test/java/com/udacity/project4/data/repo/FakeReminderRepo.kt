package com.udacity.project4.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.project4.data.model.Reminder
import com.udacity.project4.data.source.FakeReminderData
import kotlinx.coroutines.runBlocking

class FakeReminderRepo : IReminderRepo {

    private var shouldReturnError = false

    private val reminders = mutableListOf<Reminder>()
    private val observableReminders = MutableLiveData<List<Reminder>>(reminders)

    fun setReturnError(value: Boolean){
        shouldReturnError = value
    }
    override suspend fun getReminders(): List<Reminder> {
        return reminders
    }

    override suspend fun addReminder(reminder: Reminder): Long {
        reminders.add(reminder)
        refreshData()
        return reminder.id
    }

    override suspend fun getReminderWithTitle(title: String): Reminder {
        return reminders?.find { it.title == title }!!
    }

    override suspend fun refreshReminders(): List<Reminder> {
        return reminders
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        reminders.remove(reminder)
        refreshData()
    }

    override suspend fun deleteAllReminder() {
        reminders.clear()
        refreshData()
    }

    override fun getAllReminder(): LiveData<List<Reminder>> {
        return observableReminders
    }

    fun createReminder(vararg reminderList: Reminder) {
        for (reminder in reminderList) {
            reminders.add(reminder)
        }
        runBlocking { refreshReminders() }
    }
    private fun refreshData(){
        observableReminders.postValue(reminders)
    }
}