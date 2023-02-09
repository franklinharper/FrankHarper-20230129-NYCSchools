package com.franklinharper.jpmc.nycschools.data

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.franklinharper.jpmc.nycschools.ApiHighSchool
import com.franklinharper.jpmc.nycschools.Database
import com.franklinharper.jpmc.nycschools.coroutine.CoroutineDispatchers
import com.franklinharper.jpmc.nycschools.data.domain.HighSchoolWithSatScores
import com.franklinharper.jpmc.nycschools.data.restapi.ApiSatScore
import com.franklinharper.jpmc.nycschools.data.restapi.FakeApiService
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response

internal class RepositoryTest {

    private val testDispatchers = object : CoroutineDispatchers {
        override val io = Dispatchers.IO
    }

    private lateinit var database: Database

    @Before
    fun before() {
        // Start each test with an empty DB.
        val inMemorySqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(inMemorySqlDriver)
        database = Database(inMemorySqlDriver)
    }

    // TODO add more tests
    //
    // * Tests for the other repository functions. E.g. fun loadSchoolWithSatFromDb().
    // * Tests for logging of data validation anomalies.
    // * Tests for Api error responses (checking both the results returned and the logging calls).

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test loadSchoolsWithSatScores() when Rest Api responds without errors`() = runTest {
        // Arrange
        val repository = createFakeRepository(
            schoolListResponse = Response.success(
                listOf(
                    ApiHighSchool(
                        dbn = "dbn1",
                        schoolName = "school1",
                        startTime = null,
                        subway = null,
                        zipCode = null,
                        website = null,
                        totalStudents = "100",
                        transfer = null,
                    ),
                )
            ),
            satScoreListResponse = Response.success(
                listOf(
                    ApiSatScore(
                        dbn = "dbn1",
                        schoolName = "name",
                        testTakerCount = "100",
                        readingAvgScore = "100",
                        mathAvgScore = "101",
                        writingAvgScore = "102",
                    )
                )
            )
        )

        // Act
        val actualSchoolsWithSatScores = repository.loadSchoolsWithSatScores(parentScope = this)
        advanceUntilIdle()

        // Assert
        assertEquals(
            /* expected = */
            listOf(
                HighSchoolWithSatScores(
                    dbn = "dbn1",
                    name = "school1",
                    startTime = null,
                    subway = null,
                    zipCode = null,
                    website = null,
                    totalStudents = 100,
                    mathSatAverageScore = 101,
                    writingSatAverageScore = 102,
                    readingSatAverageScore = 100,
                    countOfSatTakers = 100,
                    percentageOfSatTakers = 100,
                ),
            ),
            actualSchoolsWithSatScores,
        )
    }

    private fun createFakeRepository(
        schoolListResponse: Response<List<ApiHighSchool>>,
        satScoreListResponse: Response<List<ApiSatScore>>,
    ): Repository {
        return Repository(
            dispatchers = testDispatchers,
            service = FakeApiService(
                schoolListResponse = schoolListResponse,
                satScoreListResponse = satScoreListResponse,
            ),
            database = database,
        )
    }
}