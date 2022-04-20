package com.udacity.project4.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.project4.data.model.Reminder

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminder_table")
    fun getAll():LiveData<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReminder(reminder: Reminder): Long

    @Delete
    fun delete(reminder:Reminder)

    @Query("SELECT * FROM reminder_table WHERE title=:title")
    fun getReminderWithId(title:String): Reminder

    @Query("DELETE FROM reminder_table")
    fun deleteAll()

    @Query("SELECT * FROM reminder_table")
    fun getAllReminder(): List<Reminder>
}