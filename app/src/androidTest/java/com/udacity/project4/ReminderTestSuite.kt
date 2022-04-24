package com.udacity.project4

import com.udacity.project4.data.repo.LocalDataSourceTest
import com.udacity.project4.data.repo.dao.ReminderDaoTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    ListFragmentTest::class,
    MapsFragmentTest::class,
    ToDoActivityTest::class,
    LocalDataSourceTest::class,
    ReminderDaoTest::class
)class ReminderTestSuite