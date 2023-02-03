package com.franklinharper.jpmc.nycschools.data.domain

import com.franklinharper.jpmc.nycschools.common.NA
import timber.log.Timber
import kotlin.String
import kotlin.math.roundToInt

// This class is used for internal validated data.
data class SatScores(
    val dbn: String,
    val satTestTakerCount: Long,
    val mathSatAverageScore: Long,
    val writingSatAverageScore: Long,
    val readingSatAverageScore: Long,
)