package com.udacity.project4.data.repo

import com.udacity.project4.data.model.Reminder
import com.udacity.project4.utils.wrapEspressoIdlingResource

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