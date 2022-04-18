package com.echithub.locationtodo.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.echithub.locationtodo.data.AppDatabase
import com.echithub.locationtodo.data.model.Reminder
import com.echithub.locationtodo.data.repo.IReminderRepo
import com.echithub.locationtodo.data.repo.LocalDataSource
import com.echithub.locationtodo.data.repo.ReminderRepo
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