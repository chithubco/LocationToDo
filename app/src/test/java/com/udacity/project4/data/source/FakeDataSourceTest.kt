package com.udacity.project4.data.source
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.data.model.Reminder
import com.udacity.project4.data.source.FakeReminderData.REMINDER_1
import com.udacity.project4.data.source.FakeReminderData.REMINDER_2
import com.udacity.project4.data.source.FakeReminderData.REMINDER_3
import com.udacity.project4.data.source.FakeReminderData.REMINDER_4
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.utils.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@SmallTest
class FakeDataSourceTest {
    private val localReminder = listOf(
        REMINDER_1,
        REMINDER_2,
        REMINDER_3
    )
    private val newReminder = listOf(REMINDER_4)
    private lateinit var dataSource: FakeDataSource

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup(){
        dataSource = FakeDataSource(mutableListOf())
    }

    @Test
    fun `save reminder with no title`() = runBlocking {
        val reminder = Reminder(
            1,
            "",
            "Cool Location",
            "9.052596841535514",
            "7.452365927641011",
            ""
        )
        dataSource.saveReminder(reminder)
        val value = dataSource.getAllReminder().getOrAwaitValue()
        val response = dataSource.insertReminderMessage.getOrAwaitValue()
        assertThat(response.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `save reminder with no description`() = runBlocking {
        val reminder = Reminder(
            1,
            "My Home",
            "",
            "9.052596841535514",
            "7.452365927641011",
            ""
        )
        dataSource.saveReminder(reminder)
        val value = dataSource.getAllReminder().getOrAwaitValue()
        val response = dataSource.insertReminderMessage.getOrAwaitValue()
        assertThat(response.status).isEqualTo(Status.ERROR)
    }
    @Test
    fun `save reminder with no lat`() = runBlocking {
        val reminder = Reminder(
            1,
            "My Home",
            "Cool Location",
            "",
            "7.452365927641011",
            ""
        )
        dataSource.saveReminder(reminder)
        val value = dataSource.getAllReminder().getOrAwaitValue()
        val response = dataSource.insertReminderMessage.getOrAwaitValue()
        assertThat(response.status).isEqualTo(Status.ERROR)
    }
    @Test
    fun `save reminder with no long`() = runBlocking {
        val reminder = Reminder(
            1,
            "My Home",
            "Cool Location",
            "9.052596841535514",
            "",
            ""
        )
        dataSource.saveReminder(reminder)
        val value = dataSource.getAllReminder().getOrAwaitValue()
        val response = dataSource.insertReminderMessage.getOrAwaitValue()
        assertThat(response.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `save reminder saves to list`() = runBlocking {
        dataSource.saveReminder(REMINDER_1)
        val value = dataSource.getAllReminder().getOrAwaitValue()
        assertThat(value).contains(REMINDER_1)
    }

    @Test
    fun `save 3 reminder to list and verify they are all in the list`() = runBlocking {
        dataSource.saveReminder(REMINDER_1)
        dataSource.saveReminder(REMINDER_2)
        dataSource.saveReminder(REMINDER_3)
        val value = dataSource.getAllReminder().getOrAwaitValue()
        assertThat(value).contains(REMINDER_1)
        assertThat(value).contains(REMINDER_2)
        assertThat(value).contains(REMINDER_3)
    }

    @Test
    fun `save reminder are retrieve it from the list`() = runBlocking {
        dataSource.saveReminder(REMINDER_1)
        val value = dataSource.getReminder(REMINDER_1.title)
        if (value != null) {
            assertThat(value.title).isEqualTo(REMINDER_1.title)
            assertThat(value.description).isEqualTo(REMINDER_1.description)
            assertThat(value.longitude).isEqualTo(REMINDER_1.longitude)
            assertThat(value.latitude).isEqualTo(REMINDER_1.latitude)
        }
    }

    @Test
    fun `get a reminder with empty title`() = runBlocking{
        val value = dataSource.getReminder("")
        val response = dataSource.insertReminderMessage.getOrAwaitValue()
        assertThat(response.status).isEqualTo(Status.ERROR)
    }
    /**
     * Test to delete a reminder and verify that is is not in the list
     */
    @Test
    fun `delete a reminder and veify it is not in the list`() = runBlocking {
        // GIVEN a reminder is added and deleted
        dataSource.saveReminder(REMINDER_1)

        // WHEN a reminder is deleted
        dataSource.deleteReminder(REMINDER_1)

        val value = dataSource.getAllReminder().getOrAwaitValue()
        assertThat(value).doesNotContain(REMINDER_1)
    }

    /**
     * Delete all reminders
     */
    @Test
    fun `delete all reminders`() = runBlocking{
        dataSource.deleteAllReminders()

        val value = dataSource.getAllReminder().getOrAwaitValue()

        assertThat(value).isEmpty()
    }

}