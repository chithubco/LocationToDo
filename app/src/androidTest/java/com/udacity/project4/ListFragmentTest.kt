package com.udacity.project4

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.data.model.Reminder
import com.udacity.project4.data.repo.FakeAndroidTestReminderRepo
import com.udacity.project4.data.repo.IReminderRepo
import com.udacity.project4.data.repo.source.FakeReminderData.REMINDER_1
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.not
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
            "Barumak",
            "Description for test Reminder",
            "9.052596841535514",
            "7.452365927641011",
            "2022-01-01"
        )
        repository.addReminder(activeReminder)

        launchFragmentInContainer<ListFragment>(Bundle(),R.style.Theme_LocationToDo)
//        onView(withId(R.id.tv_reminder_List)).check(matches(withText("Reminder List")))
//        onView(withId(R.id.tv_reminder_List)).check(matches(isDisplayed()))

        Thread.sleep(4000)
    }

    @Test
    fun `list_fragement_with_no_reminders_onscreen`() = runBlockingTest {
        //GIVEN The reminders list is empty
        repository.deleteAllReminder()

        //Then
        launchFragmentInContainer<ListFragment>(Bundle(),R.style.Theme_LocationToDo)

        Espresso.onView(withId(R.id.tv_no_reminder_data)).check(matches(withText("No Reminder Data Found")))
        Espresso.onView(withId(R.id.tv_no_reminder_data)).check(matches(isDisplayed()))
    }

    @Test
    fun `list_fragement_with_reminders_off_screen`() = runBlockingTest {
        //GIVEN The reminders list is empty
        repository.addReminder(REMINDER_1)

        //Then
        launchFragmentInContainer<ListFragment>(Bundle(),R.style.Theme_LocationToDo)

        Espresso.onView(withId(R.id.tv_no_reminder_data)).check(matches(withText("No Reminder Data Found")))
        Espresso.onView(withId(R.id.tv_no_reminder_data)).check(matches(not(isDisplayed())))
    }

    @Test
    fun `check_all_view_elements_are_on_screen`() = runBlockingTest {

        repository.deleteAllReminder()
        launchFragmentInContainer<ListFragment>(Bundle(),R.style.Theme_LocationToDo)
        Espresso.onView(withId(R.id.tv_view_in_map)).check(matches(isDisplayed()))
        Espresso.onView(withId(R.id.fab_add_reminder)).check(matches(isDisplayed()))
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