package com.android.code.ui.search

import androidx.lifecycle.LiveData

interface SearchRecentAdapterProperty {
    val searchedText: LiveData<String>
    fun search(text: String)
    fun removeRecentSearch(text: String)
}