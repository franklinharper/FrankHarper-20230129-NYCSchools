package com.franklinharper.jpmc.nycschools.data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.franklinharper.jpmc.nycschools.Database
import com.franklinharper.jpmc.nycschools.coroutine.CoroutineDispatchers
import com.franklinharper.jpmc.nycschools.data.domain.HighSchoolWithSatScores
import com.franklinharper.jpmc.nycschools.di.DataModule
import dagger.hilt.EntryPoints
import dagger.hilt.components.SingletonComponent
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

// The purpose of these tests is to verify the integration between the Repository
// and real REST APIs.
//
// For a production app I would run these tests
// in all of the environments: PROD, QA, DEV, etc.
//

// These tests make networking calls to the API endpoints.
// Typically they would be run at least once a day on CI.

// The data returned by the endpoints is static. So we can make assumptions
// about the data that is returned.
//
// The test assertions would be more complicated in real environments because
// the data is usually NOT static. One solution would be to to add test support
// in the backend. The backend could respond with static data when requests are
// made with a  test flag is set to "true".
//

internal class RepositoryEndToEndTest {

    private val testDispatchers = object : CoroutineDispatchers {
        override val io = Dispatchers.IO
    }

    // This is the real apiService used in the app; it makes networking calls.
    private val apiService = EntryPoints.get(
        SingletonComponent::class,
        DataModule.DataEntryPoint::class.java
    ).getapiService()

    // These dependencies are reinitialized before each test.
    private lateinit var database: Database
    private lateinit var repository: Repository

    @Before
    fun before() {
        // Start each test with an empty DB.
        val inMemorySqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(inMemorySqlDriver)
        database = Database(inMemorySqlDriver)

        repository = Repository(
            dispatchers = testDispatchers,
            service = apiService,
            database = database,
        )
    }

    // This test verifies that
    //
    // * the endpoints return the expected data
    // * the JSON data can be parsed
    // * the domain data is sorted in the correct order
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test loadSchoolsWithSatScores() when Rest Api responds without errors`() = runTest {
        // Arrange

        // No extra setup is required for this test.

        // Act
        val actualSchoolsWithSatScores = repository.loadSchoolsWithSatScores(parentScope = this)
        advanceUntilIdle()

        // Assert

        val expectedSize = 440
        assertEquals(expectedSize, actualSchoolsWithSatScores.size)

        // The assumption here is that if the first school is the
        // expected one; then the data is sorted in the correct order.
        // This check could be made more rigorous. E.g. also check
        // the last school.
        val expectedFirstItem = HighSchoolWithSatScores(
            dbn = "02M520",
            name = "Murry Bergtraum High School for Business Careers",
            startTime = "8:10am",
            zipCode = "10038",
            website = "http://schools.nyc.gov/SchoolPortals/02/M520/default.htm",
            totalStudents = 598,
            mathSatAverageScore = 440,
            writingSatAverageScore = 393,
            readingSatAverageScore = 407,
            countOfSatTakers = 264,
            percentageOfSatTakers = 44,
        )
        assertEquals(expectedFirstItem, actualSchoolsWithSatScores.first())
    }

}