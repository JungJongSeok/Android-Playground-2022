package com.android.code.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface SharedPreferencesManager {
    var recentGridSearchList: List<String>?

    var recentStaggeredSearchList: List<String>?
}

class SharedPreferencesManagerImpl @Inject constructor(@ApplicationContext private val context: Context) :
    SharedPreferencesManager {
    companion object {
        private const val RECENT_GRID_SEARCH_LIST = "recent_grid_search_list"
        private const val RECENT_STAGGERED_SEARCH_LIST = "recent_staggered_search_list"
    }

    private val sharedPreferences by lazy {
        context.getSharedPreferences(
            "sharedPreferences",
            Context.MODE_PRIVATE
        )
    }

    override var recentGridSearchList: List<String>?
        get() = sharedPreferences?.getString(RECENT_GRID_SEARCH_LIST, null)
            ?.fromJsonWithTypeToken<List<String>>()
        set(value) {
            sharedPreferences?.edit()?.let {
                if (value.isNullOrEmpty()) {
                    it.putString(RECENT_GRID_SEARCH_LIST, null)
                } else {
                    it.putString(RECENT_GRID_SEARCH_LIST, value.toJson())
                }
                it.commit()
            }
        }

    override var recentStaggeredSearchList: List<String>?
        get() = sharedPreferences?.getString(RECENT_STAGGERED_SEARCH_LIST, null)
            ?.fromJsonWithTypeToken<List<String>>()
        set(value) {
            sharedPreferences?.edit()?.let {
                if (value.isNullOrEmpty()) {
                    it.putString(RECENT_STAGGERED_SEARCH_LIST, null)
                } else {
                    it.putString(RECENT_STAGGERED_SEARCH_LIST, value.toJson())
                }
                it.commit()
            }
        }
}