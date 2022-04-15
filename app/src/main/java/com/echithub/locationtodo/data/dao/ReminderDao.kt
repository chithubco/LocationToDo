package com.echithub.locationtodo.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.echithub.locationtodo.data.model.Reminder

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminder_table")
    fun getAll():LiveData<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReminder(vararg reminders: Reminder):List<Long>

    @Delete
    fun delete(reminder:Reminder)

    @Query("SELECT * FROM reminder_table WHERE title=:title")
    fun getReminderWithId(title:String): Reminder

    @Query("DELETE FROM reminder_table")
    fun deleteAll()

    @Query("SELECT * FROM reminder_table")
    fun getAllReminder(): List<Reminder>
}