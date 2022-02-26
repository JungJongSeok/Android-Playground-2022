package com.android.code.ui.search

import androidx.lifecycle.LiveData
import com.android.code.repository.MarvelRxRepository
import com.android.code.ui.BaseViewModel
import com.android.code.util.empty
import com.android.code.util.livedata.SafetyMutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException

class SearchRxBaseViewModel(private val marvelRepository: MarvelRxRepository) :
    BaseViewModel(),
    SearchRxViewModelInput, SearchRxViewModelOutput {

    val inputs: SearchRxViewModelInput by lazy {
        this
    }
    val outputs: SearchRxViewModelOutput by lazy {
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

    private val _refreshedSwipeRefreshLayout = SafetyMutableLiveData<Boolean>()
    override val refreshedSwipeRefreshLayout: LiveData<Boolean>
        get() = _refreshedSwipeRefreshLayout

    private val initializeDataList = ArrayList<SearchData>()
    private var initializeOffset = 0
    private var initializeTotal = 0

    private var currentOffset = 0
    private var currentTotal = 0
    private var currentText: String? = null

    override fun initData(isRefreshing: Boolean) {
        val isLock = if (isRefreshing) {
            _refreshedSwipeRefreshLayout
        } else {
            _loading
        }
        if (isLock.value == true) {
            return
        }
        initSearchData()
        marvelRepository.charactersRx()
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { isLock.setValueSafety(false) }
            .subscribe({ response ->
                val recentSearchList =
                    getPreferencesRecentSearchList()?.run { SearchRecentData(this) }
                val searchDataList = response.data
                    .apply {
                        currentOffset = count
                        currentTotal = total
                    }.results?.map { SearchBaseData(it) } ?: emptyList()

                val totalList = if (recentSearchList != null) {
                    listOf(recentSearchList) + searchDataList
                } else {
                    searchDataList
                }

                initializeDataList.clear()
                initializeDataList.addAll(searchDataList)
                initializeOffset = currentOffset
                initializeTotal = currentTotal
                _responseData.setValueSafety(totalList to true)
            }, _error).addTo(compositeDisposable)
    }

    private var searchLock = PublishSubject.create<Boolean>()
    override fun search(text: String, isRefreshing: Boolean) {
        val isLock = if (isRefreshing) {
            _refreshedSwipeRefreshLayout
        } else {
            null
        }
        if (isLock?.value == true) {
            return
        }
        searchLock.onNext(true)
        initSearchData()
        currentText = text
        if (text.isEmpty()) {
            val recentSearchList =
                getPreferencesRecentSearchList()?.run { SearchRecentData(this) }
            val totalList = if (recentSearchList != null) {
                listOf(recentSearchList) + initializeDataList
            } else {
                initializeDataList
            }
            currentOffset = initializeOffset
            currentTotal = initializeTotal
            _searchedText.setValueSafety(String.empty())
            _responseData.setValueSafety(totalList to true)
            return
        }
        marvelRepository.charactersRx(nameStartsWith = text)
            .takeUntil(searchLock.firstElement().toFlowable())
            .delay(300, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { isLock?.setValueSafety(false) }
            .subscribe({ response ->
                marvelRepository.recentList =
                    (listOf(text) + (getPreferencesRecentSearchList()
                        ?: emptyList())).distinct()
                val recentSearchList =
                    getPreferencesRecentSearchList()?.run { SearchRecentData(this) }
                val searchDataList = response.data
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

                _searchedText.setValueSafety(text)
                _responseData.setValueSafety(totalList to true)
            }, {
                if (it is CancellationException) {
                    return@subscribe
                }
                _error.setValueSafety(it)
            }).addTo(compositeDisposable)
    }

    override fun canSearchMore(): Boolean {
        return currentOffset < currentTotal
    }

    override fun searchMore() {
        if (_loading.value == true) {
            return
        }
        _loading.setValueSafety(true)
        marvelRepository.charactersRx(
            nameStartsWith = currentText,
            offset = currentOffset
        )
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { _loading.setValueSafety(false) }
            .subscribe({ response ->
                val searchDataList = response.data.apply {
                    currentOffset += count
                    currentTotal = total
                }.results?.map { SearchBaseData(it) } ?: emptyList()

                val totalList = (_responseData.value?.first ?: emptyList()) + searchDataList

                _responseData.setValueSafety(totalList to false)
            }, _error).addTo(compositeDisposable)
    }

    private fun initSearchData() {
        currentOffset = 0
        currentTotal = 0
        currentText = null
    }

    override fun removeRecentSearch(text: String) {
        val recentSearchList = getPreferencesRecentSearchList()?.filter { it != text }
        marvelRepository.recentList = recentSearchList
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

    override fun getPreferencesRecentSearchList(): List<String>? {
        return marvelRepository.recentList
    }
}

interface SearchRxViewModelInput {
    fun initData(isRefreshing: Boolean = false)
    fun search(text: String, isRefreshing: Boolean = false)
    fun canSearchMore(): Boolean
    fun searchMore()
    fun removeRecentSearch(text: String)
    fun clickData(searchData: SearchData)
    fun getPreferencesRecentSearchList(): List<String>?
}

interface SearchRxViewModelOutput {
    val responseData: LiveData<Pair<List<SearchData>, Boolean>>
    val clickData: LiveData<SearchData>
    val searchedData: LiveData<SearchBaseData>
    val searchedText: LiveData<String>
    val refreshedSwipeRefreshLayout: LiveData<Boolean>
}