package com.android.code.ui.search

import com.android.code.repository.MarvelRepository

class SearchGridViewModel(override val marvelRepository: MarvelRepository) :
    SearchBaseViewModel(marvelRepository) {
    override var preferencesRecentSearchList: List<String>?
        get() = marvelRepository.recentGridSearchList
        set(value) {
            marvelRepository.recentGridSearchList = value
        }
}