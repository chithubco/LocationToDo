package com.echithub.locationtodo.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.echithub.locationtodo.data.AppDatabase
import com.echithub.locationtodo.data.model.Reminder
import com.echithub.locationtodo.data.repo.ReminderRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListViewModel(application: Application):AndroidViewModel(application) {

    lateinit var readAllData: LiveData<List<Reminder>>
    private var repo: ReminderRepo = ReminderRepo(AppDatabase.getDatabase(getApplication()))
    val hasError = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()

    init {
        readAllData = repo.readAllData
    }

    fun refresh(){
        readAllData = repo.readAllData
        Log.i("ViewModel",readAllData.value.toString())
    }

    fun addReminder(reminder: Reminder){
        viewModelScope.launch(Dispatchers.IO){
            repo.addReminder(reminder)
        }
    }

    suspend fun getReminderWithId(title: String):Reminder{
            return repo.getReminderWithTitle(title)
    }

    override fun onCleared() {
        super.onCleared()
    }
}