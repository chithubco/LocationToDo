package com.echithub.locationtodo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.echithub.locationtodo.data.dao.ReminderDao
import com.echithub.locationtodo.data.model.Reminder

@Database(entities = [Reminder::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract val reminderDao: ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /*
        Create the database
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "reminder_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}