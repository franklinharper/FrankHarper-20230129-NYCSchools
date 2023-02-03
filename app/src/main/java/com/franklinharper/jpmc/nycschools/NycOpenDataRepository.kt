package com.franklinharper.jpmc.nycschools

import com.franklinharper.jpmc.nycschools.ui.main.NycOpenDataService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import timber.log.Timber
import javax.inject.Inject

class NycOpenDataRepository @Inject constructor(
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
        // Some examples of syncing strategies:
        //
        // * every time the app launches
        // * once a year after NYC publishes an annual update
        // * when the sync hasn't been performed within the last X days.
        //

        val dataFromDb = queries.selectAllSchools().executeAsList()
        if (dataFromDb.isNotEmpty()) {
            Timber.d("Returning data from DB")
            return dataFromDb
        }
        val dataFromApi = loadFromApi(parentScope)
        saveDataToDb(dataFromApi)
        Timber.d("Returning data from API")
        return dataFromApi
    }

    private fun saveDataToDb(dataFromApi: List<HighSchoolWithSatScores>) {
        queries.transaction {
            dataFromApi.forEach { school: HighSchoolWithSatScores ->
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
                    satTestTakerCount = school.satTestTakerCount,
                )
            }
        }
    }

    private suspend fun loadFromApi(parentScope: CoroutineScope): List<HighSchoolWithSatScores> {
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
        val schoolListDeferred = parentScope.async {
            Timber.d("schoolList: start")
            // TODO error handling
            service.getSchoolList().body().also {
                Timber.d("schoolList: finish")
            }
        }
        val satScoreListDeferred = parentScope.async {
            Timber.d("satList: start")
            service.getSatScoreList().body().also {
                Timber.d("satScoreList: finish")
            }
        }

        // Execute the API requests and then merge the results.
        val schoolList = schoolListDeferred.await() ?: emptyList()
        val satScoreList = satScoreListDeferred.await() ?: emptyList()
        val satScoreMap = satScoreList.associateBy { satScore ->
            satScore.dbn
        }
        return schoolList.map { highSchool ->
            val schoolsSatScore = satScoreMap[highSchool.dbn]
            HighSchoolWithSatScores(
                dbn = highSchool.dbn,
                name = highSchool.schoolName,
                startTime = highSchool.startTime,
                subway = highSchool.subway,
                totalStudents = highSchool.totalStudents,
                zipCode = highSchool.zip,
                website = highSchool.website,
                mathSatAverageScore = schoolsSatScore?.satMathAvgScore,
                writingSatAverageScore = schoolsSatScore?.satWritingAvgScore,
                readingSatAverageScore = schoolsSatScore?.satCriticalReadingAvgScore,
                satTestTakerCount = schoolsSatScore?.satTestTakerCount,
            )
        }
    }
}
