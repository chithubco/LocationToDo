package com.udacity.project4.data.repo

import androidx.lifecycle.LiveData
import com.udacity.project4.data.model.Reminder

interface IReminderRepo {
    suspend fun getReminders(): List<Reminder>
    suspend fun addReminder(reminder: Reminder): Long
    suspend fun getReminderWithTitle(title: String): Reminder
    suspend fun refreshReminders(): List<Reminder>
    suspend fun deleteReminder(reminder: Reminder)
    suspend fun deleteAllReminder()
    fun getAllReminder():LiveData<List<Reminder>>
}