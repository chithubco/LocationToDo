package com.echithub.locationtodo.data.repo

import androidx.lifecycle.MutableLiveData
import com.echithub.locationtodo.data.model.Reminder
import com.echithub.locationtodo.data.source.FakeReminderData
import kotlinx.coroutines.runBlocking

class FakeReminderRepo: IReminderRepo {

    private val localReminder = mutableListOf<Reminder>(
        FakeReminderData.REMINDER_1,
        FakeReminderData.REMINDER_2,
        FakeReminderData.REMINDER_3
    )
    private val newReminder = listOf(FakeReminderData.REMINDER_4)

    private val observableReminders = MutableLiveData<List<Reminder>>()

    override suspend fun getReminders(): List<Reminder> {
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
    fun createReminder(vararg reminders: Reminder){
        for (reminder in reminders){
            localReminder.add(reminder)
        }
        runBlocking { refreshReminders() }
    }
}