package com.android.code.ui.sample

import androidx.lifecycle.LiveData

interface SampleSearchRecentAdapterProperty {
    val searchedText: LiveData<String>
    fun search(text: String)
    fun removeRecentSearch(text: String)
}