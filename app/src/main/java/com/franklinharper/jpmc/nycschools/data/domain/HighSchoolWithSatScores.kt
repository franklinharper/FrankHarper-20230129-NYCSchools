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
    val totalStudents: Long?,
    val satTestTakerCount: Long?,
    val mathSatAverageScore: Long?,
    val writingSatAverageScore: Long?,
    val readingSatAverageScore: Long?,
)
{
    fun satTakerPercentage(): String {
        return if (satTestTakerCount == null || totalStudents == null || totalStudents == 0L ) {
            NA
        } else {
            val percentage = (satTestTakerCount.toDouble() / totalStudents.toDouble() * 100).roundToInt()
            return percentage.toString() + "%"
        }
    }
}