package com.udacity.project4.di

import com.udacity.project4.data.dao.ReminderDao
import com.udacity.project4.data.repo.IDataSource
import com.udacity.project4.data.repo.IReminderRepo
import com.udacity.project4.data.repo.LocalDataSource
import com.udacity.project4.data.repo.ReminderRepo
import com.udacity.project4.ui.adapters.ReminderListAdapter
import com.udacity.project4.ui.viewmodel.ListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<IDataSource> {  LocalDataSource(get())}
    single<IReminderRepo> { ReminderRepo(get()) }
    viewModel{ ListViewModel(get()) }
    factory { ReminderListAdapter(get()) }
}