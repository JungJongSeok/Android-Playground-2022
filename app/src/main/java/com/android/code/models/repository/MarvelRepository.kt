package com.android.code.models.repository

import com.android.code.lib.network.MarvelService
import com.android.code.models.BaseResponse
import com.android.code.models.marvel.SampleResponse

class MarvelRepository(private val marvelService: MarvelService) {
    suspend fun characters(
        limit: Int = 10,
        apikey: String,
        timestamp: Long,
        hash: String
    ): BaseResponse<SampleResponse> {
        return marvelService.characters(limit, apikey, timestamp, hash)
    }
}