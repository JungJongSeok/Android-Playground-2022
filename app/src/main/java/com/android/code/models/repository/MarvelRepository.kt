package com.android.code.models.repository

import com.android.code.lib.network.MarvelService
import com.android.code.models.BaseResponse
import com.android.code.models.marvel.SampleResponse
import com.android.code.util.SharedPreferencesManager

class MarvelRepository(
    private val marvelService: MarvelService,
    private val sharedPreferencesManager: SharedPreferencesManager
) {
    suspend fun characters(
        nameStartsWith: String? = null,
        limit: Int = 20,
        apikey: String,
        timestamp: Long,
        hash: String
    ): BaseResponse<SampleResponse> {
        return marvelService.characters(nameStartsWith, limit, apikey, timestamp, hash)
    }

    var recentSearchList: List<String>?
        get() = sharedPreferencesManager.recentSearchList
        set(value) {
            sharedPreferencesManager.recentSearchList = value
        }
}