package com.android.code.util

import android.content.Context

class SharedPreferencesManagerImpl(private val context: Context): SharedPreferencesManager {
    companion object {
        private const val RECENT_SEARCH_LIST = "recent_search_list"
    }

    private val sharedPreferences by lazy {
        context.getSharedPreferences(
            "sharedPreferences",
            Context.MODE_PRIVATE
        )
    }

    override var recentSearchList: List<String>?
        get() = sharedPreferences?.getString(RECENT_SEARCH_LIST, null)?.fromJsonWithTypeToken<List<String>>()
        set(value) {
            sharedPreferences?.edit()?.let {
                if (value.isNullOrEmpty()) {
                    it.putString(RECENT_SEARCH_LIST, null)
                } else {
                    it.putString(RECENT_SEARCH_LIST, value.toJson())
                }
                it.commit()
            }
        }
}