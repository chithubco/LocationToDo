package com.udacity.project4.data.repo

import androidx.lifecycle.LiveData
import com.udacity.project4.data.model.Reminder

interface IDataSource {
    suspend fun getReminders(): List<Reminder>
    suspend fun refreshReminder(): List<Reminder>
    suspend fun getReminder(title: String): Reminder
    suspend fun saveReminder(reminder: Reminder): Long
    suspend fun deleteReminder(reminder: Reminder)
    suspend fun deleteAllReminders()
    fun getAllReminder(): LiveData<List<Reminder>>
}