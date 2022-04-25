package com.udacity.project4.di

import com.udacity.project4.data.dao.ReminderDao
import com.udacity.project4.data.repo.LocalDataSource
import com.udacity.project4.data.repo.ReminderRepo
import com.udacity.project4.ui.adapters.ReminderListAdapter
import com.udacity.project4.ui.viewmodel.ListViewModel
import org.koin.dsl.module

val appModule = module {
    single {  LocalDataSource(get())}
    single { ReminderRepo(get()) }
    single { ListViewModel(get()) }
    single { ReminderListAdapter(get()) }
}