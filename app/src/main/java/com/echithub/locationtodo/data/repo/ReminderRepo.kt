package com.echithub.locationtodo.data.repo

import androidx.lifecycle.LiveData
import com.echithub.locationtodo.data.AppDatabase
import com.echithub.locationtodo.data.model.Reminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderRepo(private val localDataSource: LocalDataSource) {
    private val TAG = "ReminderRepo"
//    private val reminderDao = database.reminderDao

//    val readAllData: LiveData<List<Reminder>> = localDataSource.getReminders()4\
//    private val myCoroutineScope = CoroutineScope(Dispatchers.IO)
//    val readAllData: LiveData<List<Reminder>> = localDataSource.getReminders()

    suspend fun getReminders():List<Reminder>{
        return localDataSource.getReminders()
    }

    suspend fun addReminder(vararg reminders: Reminder){
        localDataSource.saveReminder(*reminders)
    }

    suspend fun getReminderWithTitle(title:String):Reminder{
        return localDataSource.getReminder(title)
    }
}