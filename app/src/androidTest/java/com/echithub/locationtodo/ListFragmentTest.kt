package com.echithub.locationtodo

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.echithub.locationtodo.data.model.Reminder
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class ListFragmentTest {

    @Test
    fun activeReminderList_DisplayInUn(){
        val activeReminder = Reminder(1,"Test Reminder","Description for test Reminder","134,8989","78.90900","2022-01-01")

        launchFragmentInContainer<ListFragment>()
    }
}