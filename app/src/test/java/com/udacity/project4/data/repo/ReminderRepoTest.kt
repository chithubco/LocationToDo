package com.udacity.project4.data.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.data.model.Reminder
import com.udacity.project4.data.source.FakeDataSource
import com.udacity.project4.data.source.FakeReminderData.REMINDER_1
import com.udacity.project4.data.source.FakeReminderData.REMINDER_2
import com.udacity.project4.data.source.FakeReminderData.REMINDER_3
import com.udacity.project4.data.source.FakeReminderData.REMINDER_4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test


//@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class ReminderRepoTest {
    private val localReminder = listOf(REMINDER_1, REMINDER_2,REMINDER_3)
    private val newReminder = listOf(REMINDER_4)

    private lateinit var reminderDataSource:FakeDataSource


    // Class Under Test
    private lateinit var reminderRepo: ReminderRepo

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createRepository(){
        reminderDataSource = FakeDataSource(localReminder.toMutableList())
        reminderRepo = ReminderRepo(reminderDataSource)
    }


    @Test
    fun getReminders_requestAllRemindersFromDataSource() = runBlocking {
        //When
        val reminders = reminderRepo.getReminders() as List<Reminder>
        // then
        assertThat(reminders, IsEqual(localReminder))
    }

    @Test
    fun getReminder_requestAllRemindersFromDataSource() = runBlocking {
        //When
        val reminder = reminderRepo.getReminderWithTitle(REMINDER_1.title) as Reminder
        // then
        assertThat(reminder, IsEqual(REMINDER_1))
    }

    @Test
    fun getRefresh_requestAllRemindersFromDataSource() = runBlocking {
        //When
        val reminders = reminderRepo.refreshReminders() as List<Reminder>
        // then
        assertThat(reminders, IsEqual(localReminder))
    }

    @Test
    fun getSaveReminder_toDataSource() = runBlocking {
        //When
        val reminder = reminderRepo.addReminder(REMINDER_4) as Long
        // then
        assertThat(reminder, IsEqual(REMINDER_4.id))
    }

}