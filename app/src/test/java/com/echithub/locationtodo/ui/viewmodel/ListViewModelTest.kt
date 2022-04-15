package com.echithub.locationtodo.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.echithub.locationtodo.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ListViewModelTest {

    private lateinit var listViewModel: ListViewModel
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        listViewModel = ListViewModel(ApplicationProvider.getApplicationContext())
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
}