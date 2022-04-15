package com.echithub.locationtodo.data.repo

import androidx.lifecycle.LiveData
import com.echithub.locationtodo.data.model.Reminder

interface IDataSource {
    suspend fun getReminders(): List<Reminder>
    suspend fun refreshReminder(): List<Reminder>
    suspend fun getReminder(title: String): Reminder
    suspend fun saveReminder(reminder: Reminder): Long
    suspend fun deleteReminder(reminder: Reminder)
    suspend fun deleteAllReminders()
}