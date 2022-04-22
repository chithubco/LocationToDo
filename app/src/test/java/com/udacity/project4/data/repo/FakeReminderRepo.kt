package com.udacity.project4.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.project4.data.model.Reminder
import com.udacity.project4.data.source.FakeReminderData
import kotlinx.coroutines.runBlocking

class FakeReminderRepo : IReminderRepo {

    private var shouldReturnError = false

    private val localReminder = mutableListOf<Reminder>(
        FakeReminderData.REMINDER_1,
        FakeReminderData.REMINDER_2,
        FakeReminderData.REMINDER_3
    )
    private val newReminder = listOf(FakeReminderData.REMINDER_4)

    private val observableReminders = MutableLiveData<List<Reminder>>()

    fun setReturnError(value: Boolean){
        shouldReturnError = value
    }
    override suspend fun getReminders(): List<Reminder> {
//        if (shouldReturnError){
//            return Error(Exception("Test Exception"))
//        }
        return localReminder
    }

    override suspend fun addReminder(reminder: Reminder): Long {
        createReminder(reminder)
        return reminder.id
    }

    override suspend fun getReminderWithTitle(title: String): Reminder {
        return localReminder?.find { it.title == title }!!
    }

    override suspend fun refreshReminders(): List<Reminder> {
        return localReminder
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllReminder() {
        localReminder.clear()
    }

    override fun getAllReminder(): LiveData<List<Reminder>> {
        TODO("Not yet implemented")
    }

    fun createReminder(vararg reminders: Reminder) {
        for (reminder in reminders) {
            localReminder.add(reminder)
        }
        runBlocking { refreshReminders() }
    }
}