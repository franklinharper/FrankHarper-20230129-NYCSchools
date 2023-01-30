package com.franklinharper.jpmc.nycschools.ui.main

import com.franklinharper.jpmc.nycschools.HighSchool
import com.franklinharper.jpmc.nycschools.SatScore
import retrofit2.Response
import retrofit2.http.GET


interface NycOpenDataService {
    @GET("s3k6-pzi2.json")
    suspend fun getSchoolList(): Response<List<HighSchool>>
    @GET("f9bf-2cp4.json")
    suspend fun getSatScoreList(): Response<List<SatScore>>
}