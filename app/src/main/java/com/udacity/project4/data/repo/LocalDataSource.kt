package com.udacity.project4.data.repo

import androidx.lifecycle.LiveData
import com.udacity.project4.data.dao.ReminderDao
import com.udacity.project4.data.model.Reminder
import org.koin.java.KoinJavaComponent.inject

class LocalDataSource constructor(private val reminderDao: ReminderDao):IDataSource {
//class LocalDataSource constructor():IDataSource {

//    val reminderDao: ReminderDao by inject(ReminderDao::class.java)

    override suspend fun getReminders(): List<Reminder> {
        return reminderDao.getAllReminder()
    }

    override suspend fun refreshReminder(): List<Reminder> {
        return reminderDao.getAllReminder()
    }

    override suspend fun getReminder(title: String): Reminder {
        return reminderDao.getReminderWithId(title)
    }

    override suspend fun saveReminder(reminder: Reminder): Long{
        return reminderDao.insertReminder(reminder)
    }


    override suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.delete(reminder)
    }

    override suspend fun deleteAllReminders() {
        reminderDao.deleteAll()
    }

    override fun getAllReminder(): LiveData<List<Reminder>> {
        return reminderDao.getAll()
    }
}