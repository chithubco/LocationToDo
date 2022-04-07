package com.echithub.locationtodo.data.repo

import androidx.lifecycle.LiveData
import com.echithub.locationtodo.data.AppDatabase
import com.echithub.locationtodo.data.model.Reminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ReminderRepo(private val database: AppDatabase) {
    private val TAG = "ReminderRepo"
    private val reminderDao = database.reminderDao

    val readAllData: LiveData<List<Reminder>> = reminderDao.getAll()

    private val myCoroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun addReminder(vararg reminders: Reminder):List<Long>{
        return reminderDao.insertReminder(*reminders)
    }
}