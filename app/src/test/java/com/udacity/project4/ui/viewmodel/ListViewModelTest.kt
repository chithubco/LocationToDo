package com.udacity.project4.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.data.model.Reminder
import com.udacity.project4.data.repo.FakeReminderRepo
import com.udacity.project4.data.source.FakeReminderData.REMINDER_1
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.utils.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ListViewModelTest {


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var reminderRepo: FakeReminderRepo
    private lateinit var listViewModel: ListViewModel
//    private lateinit var listViewModel: ListViewModel

    @Before
    fun setupViewModel() {
        reminderRepo = FakeReminderRepo()
        listViewModel = ListViewModel(reminderRepo) // Initialize view model with Fake Repo
    }

    @Test
    fun `insert reminder with empty fields`() {
        listViewModel.makeReminder("", "", "", "", "")
        val value = listViewModel.insertReminderMessage.getOrAwaitValue()
        assertThat(value.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert reminder without title`() {
        listViewModel.makeReminder(
            "",
            "Cool Location",
            "9.052596841535514",
            "7.452365927641011",
            "2022-04-15"
        )
        val value = listViewModel.insertReminderMessage.getOrAwaitValue()
        assertThat(value.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert reminder without description`() {
        listViewModel.makeReminder(
            "My House",
            "",
            "9.052596841535514",
            "7.452365927641011",
            "2022-04-15"
        )
        val value = listViewModel.insertReminderMessage.getOrAwaitValue()
        assertThat(value.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert reminder without latitude`() {
        listViewModel.makeReminder(
            "My House",
            "Cool Location",
            "",
            "7.452365927641011",
            "2022-04-15"
        )
        val value = listViewModel.insertReminderMessage.getOrAwaitValue()
        assertThat(value.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert reminder without longitude`() {
        listViewModel.makeReminder(
            "My House",
            "Cool Location",
            "9.052596841535514",
            "",
            "2022-04-15"
        )
        val value = listViewModel.insertReminderMessage.getOrAwaitValue()
        assertThat(value.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert reminder without created date`() {
        listViewModel.makeReminder(
            "My House",
            "Cool Location",
            "9.052596841535514",
            "7.452365927641011",
            ""
        )
        val value = listViewModel.insertReminderMessage.getOrAwaitValue()
        assertThat(value.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun `inserted reminder`(){
        listViewModel.makeReminder(
            "My House",
            "Cool Location",
            "9.052596841535514",
            "7.452365927641011",
            ""
        )
        val value = listViewModel.insertReminderMessage.getOrAwaitValue()
        assertThat(value.data?.title).isEqualTo("My House")
        assertThat(value.data?.description).isEqualTo("Cool Location")
        assertThat(value.data?.latitude).isEqualTo("9.052596841535514")
        assertThat(value.data?.longitude).isEqualTo("7.452365927641011")
    }

    @Test
    fun `delete reminder with empty title`(){
        listViewModel.deleteTheReminder(
            Reminder(
                1,
                "",
                "Cool Location",
                "9.052596841535514",
                "7.452365927641011",
                ""
        ))
        val value = listViewModel.insertReminderMessage.getOrAwaitValue()
        assertThat(value.status).isEqualTo(Status.ERROR)
    }
}