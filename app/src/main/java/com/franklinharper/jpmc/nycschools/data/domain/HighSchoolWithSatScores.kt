package com.franklinharper.jpmc.nycschools.data.domain

import com.franklinharper.jpmc.nycschools.common.NA
import kotlin.String
import kotlin.math.roundToInt

// This represents internal validated data.
data class HighSchoolWithSatScores(
    val dbn: String,
    val name: String,
    val startTime: String?,
    val subway: String?,
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