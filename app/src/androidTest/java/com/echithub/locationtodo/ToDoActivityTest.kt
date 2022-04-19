package com.echithub.locationtodo

import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.echithub.locationtodo.data.repo.IReminderRepo
import com.echithub.locationtodo.data.repo.source.FakeReminderData.REMINDER_3
import com.echithub.locationtodo.utils.DataBindingIdlingResource
import com.echithub.locationtodo.utils.EspressoIdlingResource
import com.echithub.locationtodo.utils.monitorActivity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
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
//        onView(
//            allOf(withId(com.firebase.ui.auth.R.id.email),
//                isDisplayed())).perform(replaceText("michael.agbogo@echithub.com"))
//
//        onView(withId(com.firebase.ui.auth.R.id.button_next)).perform(click())
//
//        onView(allOf(withId(com.firebase.ui.auth.R.id.password)))
//            .perform(replaceText("Letmein7"))
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