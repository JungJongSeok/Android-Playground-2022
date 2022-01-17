package com.android.code.ui.search

import androidx.lifecycle.LiveData
import com.android.code.models.repository.MarvelRepository
import com.android.code.ui.BaseViewModel
import com.android.code.util.empty
import com.android.code.util.livedata.SafetyMutableLiveData
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlin.coroutines.cancellation.CancellationException

abstract class SearchBaseViewModel(open val marvelRepository: MarvelRepository) :
    BaseViewModel(),
    SearchViewModelInput, SearchViewModelOutput {

    val inputs: SearchViewModelInput by lazy {
        this
    }
    val outputs: SearchViewModelOutput by lazy {
        this
    }

    private val _responseData = SafetyMutableLiveData<Pair<List<SearchData>, Boolean>>()
    override val responseData: LiveData<Pair<List<SearchData>, Boolean>>
        get() = _responseData

    private val _clickData = SafetyMutableLiveData<SearchData>()
    override val clickData: LiveData<SearchData>
        get() = _clickData

    private val _searchedData = SafetyMutableLiveData<SearchBaseData>()
    override val searchedData: LiveData<SearchBaseData>
        get() = _searchedData

    private val _searchedText = SafetyMutableLiveData<String>()
    override val searchedText: LiveData<String>
        get() = _searchedText

    private val initializeDataList = ArrayList<SearchData>()

    abstract var preferencesRecentSearchList: List<String>?

    private var currentOffset = 0
    private var currentTotal = 0
    private var currentText: String? = null

    override fun initData() {
        launchDataLoad(
            onLoad = {
                initSearchData()
                val recentSearchList = preferencesRecentSearchList?.run { SearchRecentData(this) }
                val searchDataList = marvelRepository.characters().data
                    .apply {
                        currentOffset = count
                        currentTotal = total
                    }.results?.map { SearchBaseData(it) }
                    ?: emptyList()

                val totalList = if (recentSearchList != null) {
                    listOf(recentSearchList) + searchDataList
                } else {
                    searchDataList
                }

                initializeDataList.clear()
                initializeDataList.addAll(searchDataList)
                _responseData.setValueSafety(totalList to true)
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
                initSearchData()
                currentText = text
                if (text.isEmpty()) {
                    val recentSearchList =
                        preferencesRecentSearchList?.run { SearchRecentData(this) }
                    val totalList = if (recentSearchList != null) {
                        listOf(recentSearchList) + initializeDataList
                    } else {
                        initializeDataList
                    }
                    _searchedText.setValueSafety(String.empty())
                    _responseData.setValueSafety(totalList to true)
                    return@launchDataLoad
                }
                searchTask = async {
                    kotlin.runCatching {
                        delay(300)
                        marvelRepository.characters(nameStartsWith = text).data
                            .apply {
                                currentOffset = count
                                currentTotal = total
                            }.results?.map { SearchBaseData(it) }
                    }.onFailure {
                        _error.setValueSafety(it)
                    }.onSuccess {
                        preferencesRecentSearchList =
                            (listOf(text) + (preferencesRecentSearchList
                                ?: emptyList())).distinct()

                        val recentSearchList =
                            preferencesRecentSearchList?.run { SearchRecentData(this) }

                        val totalList = if (recentSearchList != null) {
                            listOf(recentSearchList) + (it ?: emptyList())
                        } else {
                            it ?: emptyList()
                        }

                        _searchedText.setValueSafety(text)
                        _responseData.setValueSafety(totalList to true)
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

    override fun canSearchMore(): Boolean {
        return currentOffset < currentTotal
    }

    override fun searchMore() {
        launchDataLoad(
            onLoad = {
                val searchDataList = marvelRepository.characters(
                    nameStartsWith = currentText,
                    offset = currentOffset
                ).data.apply {
                    currentOffset += count
                    currentTotal = total
                }.results?.map { SearchBaseData(it) } ?: emptyList()

                val totalList = (_responseData.value?.first ?: emptyList()) + searchDataList

                _responseData.setValueSafety(totalList to false)
            },
            onError = {
                _error.setValueSafety(it)
            }
        )
    }

    private fun initSearchData() {
        currentOffset = 0
        currentTotal = 0
        currentText = null
    }

    override fun removeRecentSearch(text: String) {
        preferencesRecentSearchList = preferencesRecentSearchList?.filter { it != text }
        val recentSearchList = preferencesRecentSearchList
        if (recentSearchList.isNullOrEmpty()) {
            val previousDataList =
                _responseData.value?.first?.filterIsInstance<SearchBaseData>() ?: emptyList()
            _responseData.setValueSafety(previousDataList to true)
        }
    }

    override fun clickData(searchData: SearchData) {
        _clickData.setValueSafety(searchData)
        if (searchData is SearchBaseData) {
            _searchedData.setValueSafety(searchData)
        }
    }
}

interface SearchViewModelInput {
    fun initData()
    fun search(text: String)
    fun canSearchMore(): Boolean
    fun searchMore()
    fun removeRecentSearch(text: String)
    fun clickData(searchData: SearchData)
}

interface SearchViewModelOutput {
    val responseData: LiveData<Pair<List<SearchData>, Boolean>>
    val clickData: LiveData<SearchData>
    val searchedData: LiveData<SearchBaseData>
    val searchedText: LiveData<String>
}