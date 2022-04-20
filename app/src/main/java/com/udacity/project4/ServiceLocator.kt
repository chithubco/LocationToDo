package com.udacity.project4

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.udacity.project4.data.AppDatabase
import com.udacity.project4.data.repo.IDataSource
import com.udacity.project4.data.repo.IReminderRepo
import com.udacity.project4.data.repo.LocalDataSource
import com.udacity.project4.data.repo.ReminderRepo
import kotlinx.coroutines.runBlocking

object ServiceLocator {

    private val lock = Any()
    private var database: AppDatabase? = null

    @Volatile
    var reminderRepo: IReminderRepo? = null

    @VisibleForTesting set

    fun provideTasksRepository(context: Context): IReminderRepo {
        synchronized(this) {
            return reminderRepo ?: createReminderRepository(context)
        }
    }

    private fun createReminderRepository(context: Context): IReminderRepo {
        val newRepo = ReminderRepo(createLocalDataSource(context))
        reminderRepo = newRepo
        return newRepo
    }

    private fun createLocalDataSource(context: Context): IDataSource {
        val database = database ?: createDataBase(context)
        return LocalDataSource(database.reminderDao)
    }

    private fun createDataBase(context: Context): AppDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "reminder_table"
        ).build()
        database = result
        return result
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                reminderRepo?.deleteAllReminder()
            }
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            reminderRepo = null
        }
    }

}