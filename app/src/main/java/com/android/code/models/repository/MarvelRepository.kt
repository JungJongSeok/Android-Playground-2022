package com.android.code.models.repository

import com.android.code.BuildConfig
import com.android.code.lib.network.MarvelService
import com.android.code.models.BaseResponse
import com.android.code.models.marvel.SampleResponse
import com.android.code.util.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class MarvelRepository(
    private val marvelService: MarvelService,
    private val sharedPreferencesManager: SharedPreferencesManager
) {
    suspend fun characters(
        nameStartsWith: String? = null,
        offset: Int = 0,
        limit: Int = 20,
    ): BaseResponse<SampleResponse> {
        val timestamp = System.currentTimeMillis()
        val hash = marvelHash(timestamp)
        return marvelService.characters(nameStartsWith, offset, limit, BuildConfig.MARVEL_PUBLIC_KEY, timestamp, hash)
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

    var recentGridSearchList: List<String>?
        get() = sharedPreferencesManager.recentGridSearchList
        set(value) {
            sharedPreferencesManager.recentGridSearchList = value
        }


    var recentStaggeredSearchList: List<String>?
        get() = sharedPreferencesManager.recentStaggeredSearchList
        set(value) {
            sharedPreferencesManager.recentStaggeredSearchList = value
        }
}