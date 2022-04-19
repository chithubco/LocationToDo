package com.echithub.locationtodo.data.repo.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.echithub.locationtodo.data.AppDatabase
import com.echithub.locationtodo.data.model.Reminder
import com.echithub.locationtodo.data.repo.source.FakeReminderData.REMINDER_1
import com.echithub.locationtodo.data.repo.source.FakeReminderData.REMINDER_2
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ReminderDaoTest {

    private lateinit var database: AppDatabase

//    @Rule
//    var instantTaskExecutorRule: InstantTaskExecutorRule()

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() {
        database.reminderDao.deleteAll()
        database.close()
    }

    @Test
    fun insertReminderAndGetById() = runBlockingTest {
        val reminder = REMINDER_1
        database.reminderDao.insertReminder(reminder)

        val loaded = database.reminderDao.getReminderWithId(reminder.title)

        assertThat(loaded as Reminder, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.title, `is`(reminder.title))
        assertThat(loaded.description, `is`(reminder.description))
        assertThat(loaded.latitude, `is`(reminder.latitude))
        assertThat(loaded.longitude, `is`(reminder.longitude))
        assertThat(loaded.createdDate, `is`(reminder.createdDate))
    }

    @Test
    fun `all_reminder_from_dao`() = runBlockingTest{
        database.reminderDao.insertReminder(REMINDER_1)
        database.reminderDao.insertReminder(REMINDER_2)

        val loaded = database.reminderDao.getAllReminder()

        assertThat(loaded.size, `is`(2))
    }

    @Test
    fun `delete_reminder_from_dao`() = runBlockingTest{

        database.reminderDao.insertReminder(REMINDER_1)
        val loaded = database.reminderDao.getAllReminder()
        assertThat(loaded.size, `is`(1))

        database.reminderDao.delete(REMINDER_1)
//        assertThat(loaded)
    }

}