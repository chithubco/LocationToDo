package com.udacity.project4.data.repo

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.data.AppDatabase
import com.udacity.project4.data.model.Reminder
import com.udacity.project4.data.repo.source.FakeReminderData.REMINDER_2
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class LocalDataSourceTest {
    private lateinit var database: AppDatabase
    private lateinit var localDataSource: IDataSource

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        localDataSource = LocalDataSource(database.reminderDao)
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun saveReminder_retrievesReminder() = runBlocking {
        // Given
        val reminder = REMINDER_2
        localDataSource.saveReminder(reminder)

        // WHEN
        val loaded = localDataSource.getReminder(reminder.title)

        // THEN
        assertThat(loaded as Reminder, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.title, `is`(reminder.title))
        assertThat(loaded.description, `is`(reminder.description))
        assertThat(loaded.latitude, `is`(reminder.latitude))
        assertThat(loaded.longitude, `is`(reminder.longitude))
        assertThat(loaded.createdDate, `is`(reminder.createdDate))
    }
}