package com.android.code.lib.network

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface AddressClientService {

    @GET
    suspend fun searchAddress(
        @Url url: String,
        @Query("currentPage") page: Int,
        @Query("keyword") query: String
    ): Unit
}