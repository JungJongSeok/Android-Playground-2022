package com.android.code.models.repository

import com.android.code.BuildConfig
import com.android.code.lib.network.MarvelService
import com.android.code.models.BaseResponse
import com.android.code.models.marvel.SampleResponse
import com.android.code.util.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest

interface MarvelRepository {
    suspend fun characters(
        nameStartsWith: String? = null,
        offset: Int = 0,
        limit: Int = 20,
    ): BaseResponse<SampleResponse>

    var recentGridSearchList: List<String>?
    var recentStaggeredSearchList: List<String>?
}

class MarvelRepositoryImpl(
    private val marvelService: MarvelService,
    private val sharedPreferencesManager: SharedPreferencesManager,
) : MarvelRepository {
    override suspend fun characters(
        nameStartsWith: String?,
        offset: Int,
        limit: Int,
    ): BaseResponse<SampleResponse> {
        val timestamp = System.currentTimeMillis()
        val hash = marvelHash(timestamp)
        val nameStarts = if (nameStartsWith.isNullOrBlank()) {
            null
        } else {
            nameStartsWith
        }
        return marvelService.characters(nameStarts,
            offset,
            limit,
            BuildConfig.MARVEL_PUBLIC_KEY,
            timestamp,
            hash)
    }

    private suspend fun marvelHash(timestamp: Long): String {
        return withContext(Dispatchers.IO) {
            val digest = MessageDigest.getInstance("MD5")
            val hashString =
                timestamp.toString() + BuildConfig.MARVEL_PRIVATE_KEY + BuildConfig.MARVEL_PUBLIC_KEY
            digest.update(hashString.encodeToByteArray())
            digest.digest().fold("") { acc, byte -> acc + String.format("%02x", byte) }
        }
    }

    override var recentGridSearchList: List<String>?
        get() = sharedPreferencesManager.recentGridSearchList
        set(value) {
            sharedPreferencesManager.recentGridSearchList = value
        }


    override var recentStaggeredSearchList: List<String>?
        get() = sharedPreferencesManager.recentStaggeredSearchList
        set(value) {
            sharedPreferencesManager.recentStaggeredSearchList = value
        }
}