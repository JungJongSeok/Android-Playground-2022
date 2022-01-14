package com.android.code.util

import android.content.Context
import okhttp3.OkHttpClient

class StethoModule {
    companion object {
        fun initializeWithDefaults(context: Context) {
            // Do not something
        }
    }
}

fun OkHttpClient.Builder.addStethoInterceptor(): OkHttpClient.Builder {
    return this
}