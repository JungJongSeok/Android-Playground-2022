package com.android.code.ui.sample

import androidx.lifecycle.LiveData
import com.bumptech.glide.RequestManager

interface SampleSearchAdapterProperty {
    val requestManager: RequestManager
    val searchedText: LiveData<String>
    fun search(text: String)
    fun removeRecentSearch(text: String)
    fun clickData(searchData: SearchData)
}