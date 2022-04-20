package com.udacity.project4.data.repo
import androidx.lifecycle.MutableLiveData
import com.udacity.project4.data.model.Reminder
import com.udacity.project4.data.repo.source.FakeReminderData.REMINDER_1
import com.udacity.project4.data.repo.source.FakeReminderData.REMINDER_2
import com.udacity.project4.data.repo.source.FakeReminderData.REMINDER_3
import com.udacity.project4.data.repo.source.FakeReminderData.REMINDER_4
import kotlinx.coroutines.runBlocking

class FakeAndroidTestReminderRepo: IReminderRepo {
    private val localReminder = mutableListOf<Reminder>(
        REMINDER_1,
        REMINDER_2,
        REMINDER_3
    )
    private val newReminder = listOf(REMINDER_4)

    private val observableReminders = MutableLiveData<List<Reminder>>()

    override suspend fun getReminders(): List<Reminder> {
        return localReminder
    }

    override suspend fun addReminder(reminder: Reminder): Long {
        createReminder(reminder)
        return reminder.id
    }

    override suspend fun getReminderWithTitle(title: String): Reminder {
        return localReminder.find { it.title == title }!!
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

    fun createReminder(vararg reminders: Reminder){
        for (reminder in reminders){
            localReminder.add(reminder)
        }
        runBlocking { refreshReminders() }
    }
}