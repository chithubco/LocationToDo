package com.udacity.project4

import com.udacity.project4.data.repo.ReminderRepoTest
import com.udacity.project4.data.source.FakeDataSourceTest
import com.udacity.project4.ui.viewmodel.ListViewModelTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    ReminderRepoTest::class,
    ListViewModelTest::class,
    FakeDataSourceTest::class

)
class ImplementationReminderTestSuite