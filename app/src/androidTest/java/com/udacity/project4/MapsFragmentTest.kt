package com.udacity.project4

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.data.repo.FakeAndroidTestReminderRepo
import com.udacity.project4.data.repo.IReminderRepo
import com.udacity.project4.utils.ToastMatcher
import dalvik.annotation.TestTarget
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
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
        launchFragmentInContainer<MapsFragment>(Bundle(), R.style.Theme_LocationToDo)
        Thread.sleep(2000)
    }

    @Test
    fun `show_dialog_test`() {
        // GIVEN
        launchFragmentInContainer<MapsFragment>(Bundle(), R.style.Theme_LocationToDo)

        // Execute and verify
        Espresso.onView(withId(R.id.map)).perform(longClick())
        Espresso.onView(withId(R.id.et_title)).perform(typeText("Best Location"))
        Espresso.onView(withId(R.id.et_title)).check(matches(isDisplayed()))
        Espresso.onView(withId(R.id.et_description)).perform(typeText("Best Description"))
        Espresso.onView(withText("ADD REMINDER")).perform(click())
    }

    fun `testing_toast_message_in_view`() {
        // GIVEN
        launchFragmentInContainer<MapsFragment>(Bundle(), R.style.Theme_LocationToDo)

        // Execute and verify
        Espresso.onView(withId(R.id.map)).perform(longClick())
        Espresso.onView(withId(R.id.et_title)).perform(typeText("Best Location"))
        Espresso.onView(withId(R.id.et_title)).check(matches(isDisplayed()))
        Espresso.onView(withId(R.id.et_description)).perform(typeText("Best Description"))
        Espresso.onView(withText("ADD REMINDER")).perform(click())
        Espresso.onView(withText("Geofence Added"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
        Thread.sleep(4000)
    }


}