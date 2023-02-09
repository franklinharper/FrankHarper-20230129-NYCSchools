package com.franklinharper.jpmc.nycschools.data.restapi

import com.franklinharper.jpmc.nycschools.ApiHighSchool
import retrofit2.Response

class FakeApiService(
    private val schoolListResponse: Response<List<ApiHighSchool>>,
    private val satScoreListResponse: Response<List<ApiSatScore>>,
) : ApiService {
    override suspend fun getSchoolList() = schoolListResponse

    override suspend fun getSatScoreList() = satScoreListResponse

}