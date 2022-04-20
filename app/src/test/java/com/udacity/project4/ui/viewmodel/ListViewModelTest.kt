package com.udacity.project4.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.udacity.project4.data.repo.FakeReminderRepo
import com.udacity.project4.data.source.FakeReminderData.REMINDER_1
import com.udacity.project4.data.source.FakeReminderData.REMINDER_2
import com.udacity.project4.data.source.FakeReminderData.REMINDER_3
import com.udacity.project4.data.source.FakeReminderData.REMINDER_4
import com.udacity.project4.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class ListViewModelTest {

    private lateinit var reminderRepo: FakeReminderRepo
    private lateinit var listViewModel: ListViewModel
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        reminderRepo = FakeReminderRepo()
        reminderRepo.createReminder(REMINDER_1, REMINDER_2, REMINDER_3,REMINDER_4)

        listViewModel = ListViewModel(reminderRepo)
    }

    @Test
    fun reminder_isLoading(){
        //Given
//        val listViewModel = ListViewModel(ApplicationProvider.getApplicationContext())
        //When
        listViewModel.isLoading.value = false
        val value = listViewModel.isLoading.getOrAwaitValue()
        //Then
        assertThat(value, not(nullValue()))
        assertThat(value, `is` (false))
    }

    @Test
    fun reminder_isError(){
        //Given
//        val listViewModel = ListViewModel(ApplicationProvider.getApplicationContext())
        //When
        listViewModel.hasError.value = false
        val value = listViewModel.hasError.getOrAwaitValue()
        //Then
        assertThat(value, not(nullValue()))
        assertThat(value, `is` (false))
    }

    @Test
    fun addReminder() = runBlocking {
        listViewModel.refresh()
        val value = listViewModel.reminders.getOrAwaitValue()
        assertThat(value,not(nullValue()))
        assertThat(value.size,`is` (7))
    }

    @Test
    fun loadReminder_loading(){
        assertThat(listViewModel.isLoading.getOrAwaitValue(),`is`(false))
        listViewModel.refresh()
        assertThat(listViewModel.isLoading.getOrAwaitValue(),`is`(true))
    }
}