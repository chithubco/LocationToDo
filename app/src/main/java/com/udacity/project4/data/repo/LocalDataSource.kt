package com.udacity.project4.data.repo

import com.udacity.project4.data.dao.ReminderDao
import com.udacity.project4.data.model.Reminder

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
        reminderDao.deleteAll()
    }
}