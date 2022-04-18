package com.echithub.locationtodo

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
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
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

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
        onView(withId(R.id.tv_reminder_List)).check(matches(withText("Reminder List")))
        onView(withId(R.id.tv_reminder_List)).check(matches(isDisplayed()))
//        Espresso.closeSoftKeyboard()
//        onView(withId(R.id.fab_add_reminder))
//            .perform(click())

        Thread.sleep(4000)
    }


    @Test
    fun click_fab_onListView_NavigateToMapFragment() = runBlockingTest {
        // Given we are on the Task Screen
        val scenario =  launchFragmentInContainer<ListFragment>(Bundle(),R.style.Theme_LocationToDo)

        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        // WHEN
        onView(withId(R.id.fab_add_reminder))
            .perform(click())

        // THEN
        verify(navController).navigate(ListFragmentDirections.actionListFragmentToMapsFragment())


    }
}