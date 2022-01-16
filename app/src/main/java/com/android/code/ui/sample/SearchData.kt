package com.android.code.ui.sample

import com.android.code.models.marvel.MarvelResult

sealed class SearchData

data class SearchBaseData(
    val result: MarvelResult
) : SearchData()

data class SearchRecentData(
    val recentList: List<String>
) : SearchData()