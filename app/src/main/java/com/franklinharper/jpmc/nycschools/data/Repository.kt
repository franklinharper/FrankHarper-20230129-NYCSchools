package com.franklinharper.jpmc.nycschools.data

import com.franklinharper.jpmc.nycschools.ApiHighSchool
import com.franklinharper.jpmc.nycschools.Database
import com.franklinharper.jpmc.nycschools.data.domain.HighSchoolWithSatScores
import com.franklinharper.jpmc.nycschools.data.domain.SatScores
import com.franklinharper.jpmc.nycschools.data.domain.toHighSchoolWithSatScores
import com.franklinharper.jpmc.nycschools.data.restapi.ApiSatScore
import com.franklinharper.jpmc.nycschools.data.restapi.NycOpenDataService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.roundToLong

class Repository @Inject constructor(
    private val service: NycOpenDataService,
    database: Database,
) {

    private val queries = database.schoolsWithSatScoresQueries

    /**
     * @param parentScope
     * @return A List of schools with SAT scores; or null if an error occurred.
     */
    suspend fun loadSchoolsWithSatScores(
        parentScope: CoroutineScope,
    ): List<HighSchoolWithSatScores> {

        // The data provided by NYC has not been updated since September 10, 2018.
        // For the purposes of this simple app we'll assume that the data is static
        // and will never be updated.
        //
        // If the data source was being updated we could sync the local DB with the data source.
        //
        // Some ideas for syncing strategies:
        //
        // * every time the app launches
        // * once a year after NYC publishes an annual update
        // * when the sync hasn't been performed within the last X days.
        // * use a light weight network call to check if the data has been updated since the last sync.
        //

        val dataFromDb = queries.getAllSchools().executeAsList()
        if (dataFromDb.isNotEmpty()) {
            Timber.d("Returning data from DB")
            return dataFromDb.map { dbHighSchool ->
                dbHighSchool.toHighSchoolWithSatScores()
            }
        }
        Timber.d("Loading data from API")
        val (apiHighSchoolList, apiSatScoreList) = loadFromApi(parentScope)
        // TODO use threading so that the post processing below is NOT done on the main thread.
        val validatedSchoolsWithSatScores = validateApiDataAndLogAnomalies(
            apiHighSchoolList,
            apiSatScoreList,
        )
        saveDataToDb(validatedSchoolsWithSatScores)
        return validatedSchoolsWithSatScores
    }

    private fun validateApiDataAndLogAnomalies(
        apiHighSchoolList: List<ApiHighSchool>,
        apiSatScoreList: List<ApiSatScore>,
    ): List<HighSchoolWithSatScores> {
        val validatedSatScoreMap = apiSatScoreList.mapNotNull { apiSatScore: ApiSatScore ->
            val validatedTestTakerCount = apiSatScore.testTakerCount?.toLongOrNull()
            val validatedAvgReadingScore = apiSatScore.readingAvgScore?.toLongOrNull()
            val validatedAvgMathScore = apiSatScore.mathAvgScore?.toLongOrNull()
            val validatedAvgWritingScore = apiSatScore.writingAvgScore?.toLongOrNull()
            if (
                apiSatScore.dbn.isNullOrBlank()
                || apiSatScore.schoolName.isNullOrBlank()
                || validatedTestTakerCount == null
                || validatedAvgReadingScore == null
                || validatedAvgMathScore == null
                || validatedAvgWritingScore == null
            ) {
                Timber.w("Received invalid SAT data from REST API: $apiSatScore")
                null
            } else {
                SatScores(
                    apiSatScore.dbn,
                    mathSatAverageScore = validatedAvgMathScore,
                    writingSatAverageScore = validatedAvgWritingScore,
                    readingSatAverageScore = validatedAvgReadingScore,
                    satTestTakerCount = validatedTestTakerCount
                )
            }
        }.associateBy { satScore ->
            // We aren't checking for duplicate "dbn" values coming from the API.
            // So if the list contains elements with duplicate "dbn"s then only the last one
            // will be in the Map.
            satScore.dbn
        }

        val validatedSchoolList = apiHighSchoolList.mapNotNull { apiSchool: ApiHighSchool ->
            val validatedTotalStudents = apiSchool.totalStudents?.toLongOrNull()
            val validatedSatScores = validatedSatScoreMap[apiSchool.dbn]
            if (
                apiSchool.dbn.isNullOrBlank()
                || apiSchool.schoolName.isNullOrBlank()
                // school.startTime: no validation; it is an optional unformatted String
                // school.subway: no validation; but in a real app we could do validation
                // school.zipCode: no validation; but in a real app we would validate
                // school.website: no validation; but in a real app we would do validate
                || validatedTotalStudents == null
            ) {
                Timber.w("Received invalid school data from REST API: $apiSchool, $validatedSatScores")
                null
            } else {
                val validatedSatTakerPercentage = if (validatedSatScores == null) {
                    null
                } else {
                    val ratio =
                        validatedSatScores.satTestTakerCount.toDouble() / validatedTotalStudents.toDouble()
                    (ratio * 100).roundToLong()
                }
                HighSchoolWithSatScores(
                    dbn = apiSchool.dbn,
                    name = apiSchool.schoolName,
                    startTime = apiSchool.startTime,
                    subway = apiSchool.subway,
                    zipCode = apiSchool.zipCode,
                    website = apiSchool.website,
                    totalStudents = validatedTotalStudents,
                    countOfSatTakers = validatedSatScores?.satTestTakerCount,
                    percentageOfSatTakers = validatedSatTakerPercentage,
                    mathSatAverageScore = validatedSatScores?.mathSatAverageScore,
                    writingSatAverageScore = validatedSatScores?.writingSatAverageScore,
                    readingSatAverageScore = validatedSatScores?.readingSatAverageScore,
                )
            }
        }

        return validatedSchoolList
    }

    private fun saveDataToDb(validatedData: List<HighSchoolWithSatScores>) {
        queries.transaction {
            validatedData.forEach { school: HighSchoolWithSatScores ->
                queries.insert(
                    dbn = school.dbn,
                    name = school.name,
                    startTime = school.startTime,
                    subway = school.subway,
                    totalStudents = school.totalStudents,
                    zipCode = school.zipCode,
                    website = school.website,
                    mathSatAverageScore = school.mathSatAverageScore,
                    writingSatAverageScore = school.writingSatAverageScore,
                    readingSatAverageScore = school.readingSatAverageScore,
                    satTestTakerCount = school.countOfSatTakers,
                    satTestTakerPercentage = school.percentageOfSatTakers,
                )
            }
        }
    }

    private suspend fun loadFromApi(parentScope: CoroutineScope): Pair<List<ApiHighSchool>, List<ApiSatScore>> {
        //
        // The getSchoolList() API call returns a significant amount of data.
        //
        // Instead of cancelling the requests when the parentScope is no longer
        // active; we'll always let the API requests finish and store the data in the DB
        // for future usage.
        //
        // The benefit is that when the user relaunches the app; the data will be loaded from the DB.
        // This is faster and more reliable than making networking calls.
        //
        // Passing in the parentScope enables revisiting this decision
        // without making a change to this function's signature.
        //
        // ---------------------------------------------------------------------------------
        //
        // Execute the networking calls asynchronously.
        val apiSchoolListDeferred = parentScope.async {
            Timber.d("schoolList: start")
            service.getSchoolList().body().also { highSchoolListFromApi: List<ApiHighSchool>? ->
                Timber.d("schoolList finish count: ${highSchoolListFromApi?.size}")
            }
        }
        val apiSatScoreListDeferred = parentScope.async {
            Timber.d("satList: start")
            service.getSatScoreList().body().also { satScoreListFromApi: List<ApiSatScore>? ->
                Timber.d("satScoreList finish count: ${satScoreListFromApi?.size}")
            }
        }

        // Execute the API requests in parallel and return the results.
        return Pair(
            apiSchoolListDeferred.await() ?: emptyList(),
            apiSatScoreListDeferred.await() ?: emptyList(),
        )
    }

    fun loadSchoolWithSatFromDb(dbn: String): HighSchoolWithSatScores =
        queries
            .getSchoolByDbn(dbn)
            .executeAsOne()
            .toHighSchoolWithSatScores()
}
