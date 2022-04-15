package com.echithub.locationtodo.data.repo

import androidx.lifecycle.LiveData
import com.echithub.locationtodo.data.dao.ReminderDao
import com.echithub.locationtodo.data.model.Reminder

class LocalDataSource constructor(private val reminderDao: ReminderDao):IDataSource {

    override suspend fun getReminders(): List<Reminder> {
        return reminderDao.getAllReminder()
    }

    override suspend fun refreshReminder(): List<Reminder> {
        TODO("Not yet implemented")
    }

    override suspend fun getReminder(title: String): Reminder {
        return reminderDao.getReminderWithId(title)
    }

    override suspend fun saveReminder(reminder: Reminder): Long{
        return reminderDao.insertReminder(reminder)
    }


    override suspend fun deleteReminder(reminder: Reminder) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllReminders() {
        TODO("Not yet implemented")
    }
}