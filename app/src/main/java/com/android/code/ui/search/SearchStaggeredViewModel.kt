package com.android.code.ui.search

import com.android.code.models.repository.MarvelRepository

class SearchStaggeredViewModel(override val marvelRepository: MarvelRepository) :
    SearchBaseViewModel(marvelRepository) {
    override var preferencesRecentSearchList: List<String>?
        get() = marvelRepository.recentStaggeredSearchList
        set(value) {
            marvelRepository.recentStaggeredSearchList = value
        }
}