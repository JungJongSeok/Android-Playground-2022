package com.android.code.util

import android.content.Context
import okhttp3.OkHttpClient

class FlipperModule {
    companion object {
        fun initialize(context: Context) {
            // Do not something
        }
    }
}

fun OkHttpClient.Builder.addFlipperInterceptor(): OkHttpClient.Builder {
    return this
}