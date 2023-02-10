package com.franklinharper.jpmc.nycschools.data.domain

import com.franklinharper.jpmc.nycschools.HighSchool

// This represents internal validated data.
data class HighSchoolWithSatScores(
    val dbn: String,
    val name: String,
    val startTime: String?,
    val zipCode: String?,
    val website: String?,
    // Using Longs because that's what corresponds to an INTEGER in SqlLite.
    // For details see Db schema in SchoolsWithSatScores.sq (path -> app/src/main/sqldelight/...).
    val totalStudents: Long?,
    val mathSatAverageScore: Long?,
    val writingSatAverageScore: Long?,
    val readingSatAverageScore: Long?,
    val countOfSatTakers: Long?,
    val percentageOfSatTakers: Long?,
)

fun HighSchool.toHighSchoolWithSatScores(): HighSchoolWithSatScores =
    HighSchoolWithSatScores(
        dbn = dbn,
        name = name,
        startTime = startTime,
        totalStudents = totalStudents,
        zipCode = zipCode,
        website = website,
        mathSatAverageScore = mathSatAverageScore,
        writingSatAverageScore = writingSatAverageScore,
        readingSatAverageScore = readingSatAverageScore,
        countOfSatTakers = satTestTakerCount,
        percentageOfSatTakers = satTestTakerPercentage
    )