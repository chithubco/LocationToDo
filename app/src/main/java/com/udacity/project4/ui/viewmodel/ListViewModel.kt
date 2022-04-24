package com.udacity.project4.ui.viewmodel

import androidx.lifecycle.*
import com.udacity.project4.data.model.Reminder
import com.udacity.project4.data.repo.IReminderRepo
import com.udacity.project4.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListViewModel(private var repo: IReminderRepo) : ViewModel() {

    val reminderList = repo.getAllReminder()
    val hasError = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()

    private val _reminders = MutableLiveData<Resource<List<Reminder>>>()
    val reminders: LiveData<Resource<List<Reminder>>>
        get() = _reminders

    private val _insetReminderMsg = MutableLiveData<Resource<Reminder>>()
    val insertReminderMessage: LiveData<Resource<Reminder>>
        get() = _insetReminderMsg

    init {
        refresh()
    }

    fun refresh() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val remindersList = repo.getReminders()
            isLoading.postValue(false)
        }
    }

    fun addReminder(reminder: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        repo.addReminder(reminder)
    }

    fun deleteReminder(reminder: Reminder) = viewModelScope.launch(Dispatchers.IO) {
        repo.deleteReminder(reminder)
    }

    suspend fun getReminderWithId(title: String): Reminder {
        return repo.getReminderWithTitle(title)
    }

    fun makeReminder(
        title: String,
        description: String,
        lat: String,
        long: String,
        createdDate: String
    ) {
        if (title.isEmpty() || description.isEmpty() || lat.isEmpty() || long.isEmpty()) {
            _insetReminderMsg.postValue(Resource.error("Enter title, description, lat ,long", null))
            return
        }
        val reminder =
            Reminder(
                id = 0,
                title = title,
                description = description,
                latitude = lat,
                longitude = long,
                createdDate = createdDate
            )
        addReminder(reminder)
        _insetReminderMsg.postValue(Resource.success(reminder))
    }

    fun deleteTheReminder(reminder: Reminder) {
        if (!validateReminder(reminder)) {
            _insetReminderMsg.postValue(Resource.error("Empty Reminder", null))
            return
        }
        deleteReminder(reminder)
        _insetReminderMsg.postValue(Resource.success(reminder))
    }

    private fun validateReminder(reminder: Reminder): Boolean {
        if (reminder.title.isNullOrEmpty() || reminder.description.isNullOrEmpty()
            || reminder.latitude.isNullOrEmpty() || reminder.longitude.isNullOrEmpty()
        ) {
            return false
        }
        return true
    }


    override fun onCleared() {
        super.onCleared()
    }

    @Suppress("UNCHECKED_CAST")
    class ListViewModelFactory(
        private val reminderRepository: IReminderRepo
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            (ListViewModel(reminderRepository) as T)
    }
}