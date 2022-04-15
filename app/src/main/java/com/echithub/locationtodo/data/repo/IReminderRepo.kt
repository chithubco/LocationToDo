package com.echithub.locationtodo.data.repo

import com.echithub.locationtodo.data.model.Reminder

interface IReminderRepo {
    suspend fun getReminders(): List<Reminder>
    suspend fun addReminder(reminder: Reminder): Long
    suspend fun getReminderWithTitle(title: String): Reminder
    suspend fun refreshReminders(): List<Reminder>
    suspend fun deleteReminder(reminder: Reminder)
}