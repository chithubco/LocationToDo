package com.echithub.locationtodo

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.echithub.locationtodo.data.repo.FakeAndroidTestReminderRepo
import com.echithub.locationtodo.data.repo.IReminderRepo
import com.echithub.locationtodo.data.repo.source.FakeReminderData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class MapsFragmentTest {
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
    fun activeMapsFragment() = runBlockingTest {
        launchFragmentInContainer<MapsFragment>(Bundle(),R.style.Theme_LocationToDo)
        Thread.sleep(2000)
    }


}