package com.udacity.project4.ui.viewmodel

import androidx.lifecycle.*
import com.udacity.project4.data.model.Reminder
import com.udacity.project4.data.repo.IReminderRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListViewModel(private var repo: IReminderRepo):ViewModel() {

    val hasError = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    val reminders = MutableLiveData<List<Reminder>>()

    init {
        refresh()
    }

    fun refresh(){
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO){
            val remindersList = repo.getReminders()
            remindersRetrieved(remindersList)
            isLoading.postValue(false)
        }
    }

    fun addReminder(reminder: Reminder){
        viewModelScope.launch(Dispatchers.IO){
            repo.addReminder(reminder)
        }
    }

    suspend fun getReminderWithId(title: String):Reminder{
            return repo.getReminderWithTitle(title)
    }

    // Assign values
    private fun remindersRetrieved(remindersList: List<Reminder>){
        reminders.postValue(remindersList)
//        isLoading.value = false
//        hasError.value = false
    }

    override fun onCleared() {
        super.onCleared()
    }
    @Suppress("UNCHECKED_CAST")
    class ListViewModelFactory (
        private val reminderRepository: IReminderRepo
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            (ListViewModel(reminderRepository) as T)
    }
}