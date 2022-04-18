package com.echithub.locationtodo.data.repo

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.echithub.locationtodo.data.AppDatabase
import com.echithub.locationtodo.data.model.Reminder
import com.echithub.locationtodo.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Wrapper

class ReminderRepo constructor(private val localDataSource: IDataSource) : IReminderRepo {

    override suspend fun getReminders():List<Reminder>{
        wrapEspressoIdlingResource {
            return localDataSource.getReminders()
        }
    }

    override suspend fun addReminder(reminder: Reminder): Long{
        wrapEspressoIdlingResource {
            return localDataSource.saveReminder(reminder)
        }

    }

    override suspend fun getReminderWithTitle(title:String):Reminder{
        wrapEspressoIdlingResource {
            return localDataSource.getReminder(title)
        }

    }

    override suspend fun refreshReminders():List<Reminder>{
        wrapEspressoIdlingResource {
            return localDataSource.refreshReminder()
        }

    }

    override suspend fun deleteReminder(reminder: Reminder){
        wrapEspressoIdlingResource {
            localDataSource.deleteReminder(reminder)
        }

    }

    override suspend fun deleteAllReminder() {
        wrapEspressoIdlingResource {
            localDataSource.deleteAllReminders()
        }

    }
}