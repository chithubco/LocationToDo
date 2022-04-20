package com.udacity.project4

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.data.repo.IReminderRepo
import com.udacity.project4.data.repo.source.FakeReminderData.REMINDER_3
import com.udacity.project4.utils.DataBindingIdlingResource
import com.udacity.project4.utils.EspressoIdlingResource
import com.udacity.project4.utils.monitorActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class ToDoActivityTest {

    private lateinit var repository: IReminderRepo
    private val dataBindingIdlingResource = DataBindingIdlingResource()
    @Before
    fun init(){
        repository = ServiceLocator.provideTasksRepository(getApplicationContext())
        runBlocking {
            repository.deleteAllReminder()
        }
    }

    @After
    fun reset(){
        ServiceLocator.resetRepository()
    }
    @Before
    fun registerIdlingResource(){

        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun editReminder() = runBlocking {
        repository.addReminder(REMINDER_3)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.btn_login)).perform(click())

//        onView(withId(com.firebase.ui.auth.R.id.email_button)).perform(click())
//        onView(
//            allOf(withId(com.firebase.ui.auth.R.id.email),
//                isDisplayed()));
//
//        onView(withId(com.firebase.ui.auth.R.id.button_sign_in)).perform(click())

//        onView(withId(com.firebase.ui.auth.R.id.email)).perform(replaceText("michael.agbogo@echithub.com"))
//        Thread.sleep(2000)
//        onView(withText("Reminder List")).check(matches(isDisplayed()));
//        onView(withId(R.id.fab_add_reminder)).perform(click())
//        Thread.sleep(2000)
        activityScenario.close()
    }


}