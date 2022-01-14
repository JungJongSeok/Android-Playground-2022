package com.android.code.lib.network

import com.android.code.models.BaseResponse
import com.android.code.models.marvel.SampleResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MarvelService {

    @GET("characters")
    suspend fun characters(
        @Query("limit") limit: Int,
        @Query("apikey") apikey: String,
        @Query("ts") timestamp: Long,
        @Query("hash") hash: String
    ): BaseResponse<SampleResponse>
}