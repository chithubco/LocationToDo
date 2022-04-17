package com.echithub.locationtodo

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.echithub.locationtodo.data.model.Reminder
import com.echithub.locationtodo.data.repo.FakeAndroidTestReminderRepo
import com.echithub.locationtodo.data.repo.IReminderRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class ListFragmentTest {
    private lateinit var repository: IReminderRepo

    @Before
    fun initRepository() {
        repository = FakeAndroidTestReminderRepo()
        ServiceLocator.reminderRepo = repository
    }

    @After
    fun cleanupDb() = runBlockingTest {
        ServiceLocator.resetRepository()
    }

    @Test
    fun activeReminderList_DisplayInUn() = runBlockingTest {
        val activeReminder = Reminder(
            1,
            "Test Reminder",
            "Description for test Reminder",
            "134,8989",
            "78.90900",
            "2022-01-01"
        )
        repository.addReminder(activeReminder)

        launchFragmentInContainer<ListFragment>(Bundle(),R.style.Theme_LocationToDo)
        Thread.sleep(4000)
    }
}