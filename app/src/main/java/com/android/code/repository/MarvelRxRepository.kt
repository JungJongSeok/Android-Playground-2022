package com.android.code.repository

import com.android.code.BuildConfig
import com.android.code.lib.network.MarvelService
import com.android.code.models.BaseResponse
import com.android.code.models.marvel.SampleResponse
import com.android.code.ui.search.SearchType
import com.android.code.util.SharedPreferencesManager
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.security.MessageDigest


interface MarvelRxRepository {
    fun charactersRx(
        nameStartsWith: String? = null,
        offset: Int = 0,
        limit: Int = 20,
    ): Single<BaseResponse<SampleResponse>>

    var recentList: List<String>?
}

class MarvelRxRepositoryImpl(
    private val marvelService: MarvelService,
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val type: SearchType,
) : MarvelRxRepository {
    override fun charactersRx(
        nameStartsWith: String?,
        offset: Int,
        limit: Int,
    ): Single<BaseResponse<SampleResponse>> {
        return Single.fromCallable {
            val timestamp = System.currentTimeMillis()
            val hash = marvelHash(timestamp).blockingGet()
            val nameStarts = if (nameStartsWith.isNullOrBlank()) {
                null
            } else {
                nameStartsWith
            }
            Triple(timestamp, hash, nameStarts)
        }
            .subscribeOn(Schedulers.io())
            .flatMap { (timestamp, hash, nameStarts) ->
                marvelService.charactersRx(nameStarts,
                    offset,
                    limit,
                    BuildConfig.MARVEL_PUBLIC_KEY,
                    timestamp,
                    hash)
            }
    }

    private fun marvelHash(timestamp: Long): Single<String> {
        return Single.fromCallable {
            val digest = MessageDigest.getInstance("MD5")
            val hashString =
                timestamp.toString() + BuildConfig.MARVEL_PRIVATE_KEY + BuildConfig.MARVEL_PUBLIC_KEY
            digest.update(hashString.encodeToByteArray())
            digest.digest().fold("") { acc, byte -> acc + String.format("%02x", byte) }
        }.subscribeOn(Schedulers.io())
    }

    override var recentList: List<String>?
        get() {
            return when (type) {
                SearchType.GRID -> sharedPreferencesManager.recentGridSearchList
                SearchType.STAGGERED -> sharedPreferencesManager.recentStaggeredSearchList
            }
        }
        set(value) {
            when (type) {
                SearchType.GRID -> sharedPreferencesManager.recentGridSearchList = value
                SearchType.STAGGERED -> sharedPreferencesManager.recentStaggeredSearchList = value
            }
        }
}