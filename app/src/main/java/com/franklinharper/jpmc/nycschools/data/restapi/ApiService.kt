package com.franklinharper.jpmc.nycschools.data.restapi

import com.franklinharper.jpmc.nycschools.ApiHighSchool
import retrofit2.Response
import retrofit2.http.GET


interface ApiService {
    @GET("s3k6-pzi2.json")
    suspend fun getSchoolList(): Response<List<ApiHighSchool>>
    @GET("f9bf-2cp4.json")
    suspend fun getSatScoreList(): Response<List<ApiSatScore>>
}