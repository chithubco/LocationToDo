package com.echithub.locationtodo.data.repo

import androidx.lifecycle.LiveData
import com.echithub.locationtodo.data.AppDatabase
import com.echithub.locationtodo.data.model.Reminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderRepo constructor(private val localDataSource: IDataSource) {

    suspend fun getReminders():List<Reminder>{
        return localDataSource.getReminders()
    }

    suspend fun addReminder(reminder: Reminder): Long{
        return localDataSource.saveReminder(reminder)
    }

    suspend fun getReminderWithTitle(title:String):Reminder{
        return localDataSource.getReminder(title)
    }

    suspend fun refreshReminders():List<Reminder>{
        return localDataSource.refreshReminder()
    }

    suspend fun deleteReminder(reminder: Reminder){
        localDataSource.deleteReminder(reminder)
    }
}