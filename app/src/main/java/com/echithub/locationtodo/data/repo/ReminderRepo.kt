package com.echithub.locationtodo.data.repo

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.echithub.locationtodo.data.AppDatabase
import com.echithub.locationtodo.data.model.Reminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderRepo constructor(private val localDataSource: IDataSource) : IReminderRepo {

    override suspend fun getReminders():List<Reminder>{
        return localDataSource.getReminders()
    }

    override suspend fun addReminder(reminder: Reminder): Long{
        return localDataSource.saveReminder(reminder)
    }

    override suspend fun getReminderWithTitle(title:String):Reminder{
        return localDataSource.getReminder(title)
    }

    override suspend fun refreshReminders():List<Reminder>{
        return localDataSource.refreshReminder()
    }

    override suspend fun deleteReminder(reminder: Reminder){
        localDataSource.deleteReminder(reminder)
    }

    companion object {
        @Volatile
        private var INSTANCE: ReminderRepo? = null

        fun getRepository(app: Application): ReminderRepo {
            return INSTANCE ?: synchronized(this) {
                val database = Room.databaseBuilder(app,
                    AppDatabase::class.java, "reminder_table")
                    .build()
                ReminderRepo(LocalDataSource(database.reminderDao)).also {
                    INSTANCE = it
                }
            }
        }
    }
}