package com.android.code.ui.sample

import androidx.lifecycle.LiveData
import com.android.code.BuildConfig
import com.android.code.models.repository.MarvelRepository
import com.android.code.ui.BaseViewModel
import com.android.code.util.empty
import com.android.code.util.livedata.SafetyMutableLiveData
import kotlinx.coroutines.*
import java.security.MessageDigest
import kotlin.coroutines.cancellation.CancellationException

class SampleViewModel(private val marvelRepository: MarvelRepository) : BaseViewModel(),
    SampleViewModelInput, SampleViewModelOutput {

    val inputs: SampleViewModelInput = this
    val outputs: SampleViewModelOutput = this

    private val _responseData = SafetyMutableLiveData<List<SearchData>>()
    override val responseData: LiveData<List<SearchData>>
        get() = _responseData

    private val _recentSearchList = SafetyMutableLiveData<List<String>>()
    override val recentSearchList: LiveData<List<String>>
        get() = _recentSearchList

    private val _clickData = SafetyMutableLiveData<SearchData>()
    override val clickData: LiveData<SearchData>
        get() = _clickData

    private val _searchedText = SafetyMutableLiveData<String>()
    override val searchedText: LiveData<String>
        get() = _searchedText

    private val initializeDataList = ArrayList<SearchData>()

    override fun initData() {
        launchDataLoad(
            onLoad = {
                val recentSearchList =
                    marvelRepository.recentSearchList?.run { SearchRecentData(this) }

                val timestamp = System.currentTimeMillis()
                val hash = marvelHash(timestamp)
                val searchDataList = marvelRepository.characters(
                    apikey = BuildConfig.MARVEL_PUBLIC_KEY,
                    timestamp = timestamp, hash = hash
                ).data.results?.map { SearchBaseData(it) } ?: emptyList()

                val totalList = if (recentSearchList != null) {
                    listOf(recentSearchList) + searchDataList
                } else {
                    searchDataList
                }

                initializeDataList.clear()
                initializeDataList.addAll(searchDataList)
                _responseData.setValueSafety(totalList)
            },
            onError = {
                _error.setValueSafety(it)
            }
        )
    }

    private var searchTask: Deferred<Unit>? = null
    override fun search(text: String) {
        launchDataLoad(
            null,
            onLoad = {
                searchTask?.cancel()
                if (text.isEmpty()) {
                    val recentSearchList =
                        marvelRepository.recentSearchList?.run { SearchRecentData(this) }
                    val totalList = if (recentSearchList != null) {
                        listOf(recentSearchList) + initializeDataList
                    } else {
                        initializeDataList
                    }
                    _searchedText.setValueSafety(String.empty())
                    _responseData.setValueSafety(totalList)
                    return@launchDataLoad
                }
                searchTask = async {
                    kotlin.runCatching {
                        delay(300)
                        val timestamp = System.currentTimeMillis()
                        val hash = marvelHash(timestamp)
                        marvelRepository.characters(
                            nameStartsWith = text,
                            apikey = BuildConfig.MARVEL_PUBLIC_KEY,
                            timestamp = timestamp, hash = hash
                        ).data.results?.map { SearchBaseData(it) }
                    }.onFailure {
                        _error.setValueSafety(it)
                    }.onSuccess {
                        marvelRepository.recentSearchList =
                            (listOf(text) + (marvelRepository.recentSearchList
                                ?: emptyList())).distinct()

                        val recentSearchList =
                            marvelRepository.recentSearchList?.run { SearchRecentData(this) }

                        val totalList = if (recentSearchList != null) {
                            listOf(recentSearchList) + (it ?: emptyList())
                        } else {
                            it ?: emptyList()
                        }

                        _searchedText.setValueSafety(text)
                        _responseData.setValueSafety(totalList)
                    }
                }
                searchTask?.await()
            },
            onError = {
                if (it is CancellationException) {
                    return@launchDataLoad
                }
                _error.setValueSafety(it)
            }
        )
    }

    override fun removeRecentSearch(text: String) {
        marvelRepository.recentSearchList = marvelRepository.recentSearchList?.filter { it != text }
        val recentSearchList = marvelRepository.recentSearchList
        if (recentSearchList.isNullOrEmpty()) {
            val previousDataList = _responseData.value?.filterIsInstance<SearchBaseData>() ?: emptyList()
            _responseData.setValueSafety(previousDataList)
        }
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

    override fun clickData(searchData: SearchData) {
        _clickData.setValueSafety(searchData)
    }
}

interface SampleViewModelInput {
    fun initData()
    fun search(text: String)
    fun removeRecentSearch(text: String)
    fun clickData(searchData: SearchData)
}

interface SampleViewModelOutput {
    val responseData: LiveData<List<SearchData>>
    val recentSearchList: LiveData<List<String>>
    val clickData: LiveData<SearchData>
    val searchedText: LiveData<String>
}