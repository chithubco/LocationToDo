package com.udacity.project4.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.project4.data.model.Reminder
import com.udacity.project4.data.repo.IDataSource
import com.udacity.project4.utils.Resource

class FakeDataSource(private var reminders:MutableList<Reminder>? = mutableListOf()): IDataSource {

    private val reminderListLiveData = MutableLiveData<List<Reminder>>(reminders)

    private val _insetReminderMsg = MutableLiveData<Resource<Reminder>>()
    val insertReminderMessage: LiveData<Resource<Reminder>>
        get() = _insetReminderMsg

    override suspend fun getReminders(): List<Reminder>{
        return reminders!!
    }

    override suspend fun getReminder(title: String): Reminder? {
        if (title.isNullOrEmpty()){
            _insetReminderMsg.postValue(Resource.error("Empty Title", null))
            return null
        }
        val reminderToReturn = reminders?.find { it.title == title }
        if (reminderToReturn == null){
            _insetReminderMsg.postValue(Resource.error("No Reminder Found", null))
            return null
        }
        return reminderToReturn
    }

    override suspend fun refreshReminder(): List<Reminder> {
        return reminders!!
    }

    override suspend fun saveReminder(reminder: Reminder): Long{
        if (isValidReminder(reminder)){
            reminders?.add(reminder)
            val reminderToReturn = reminders?.find { it.title == reminder.title }!!
            _insetReminderMsg.postValue(Resource.success(reminderToReturn))
            refreshData()
            return reminderToReturn.id
        }
        _insetReminderMsg.postValue(Resource.error("Empty Reminder", null))
        return -1
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        if (isValidReminder(reminder)){
            reminders?.remove(reminder)
            refreshData()
            _insetReminderMsg.postValue(Resource.success(null))
            return
        }
        _insetReminderMsg.postValue(Resource.error("Empty Reminder", null))
        return
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
        refreshData()
        _insetReminderMsg.postValue(Resource.success(null))
    }

    override fun getAllReminder(): LiveData<List<Reminder>> {
        return reminderListLiveData
    }
    private fun refreshData(){
        reminderListLiveData.postValue(reminders)
    }

    private fun isValidReminder(reminder: Reminder):Boolean{
        if (reminder.title.isNullOrEmpty()) return false
        if (reminder.description.isNullOrEmpty()) return false
        if (reminder.longitude.isNullOrEmpty()) return false
        if (reminder.latitude.isNullOrEmpty()) return false
        return true
    }
}