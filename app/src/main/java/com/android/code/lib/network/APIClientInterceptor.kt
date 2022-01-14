package com.android.code.lib.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

open class APIClientInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response =
        response(chain.proceed(request(chain.request())))

    private fun request(request: Request): Request =
        request.newBuilder()
            .addHeader("Content-Type", "application/json;charset=UTF-8")
            .url(request.url)
            .build()

    private fun response(response: Response): Response {
        return response
            .newBuilder()
            .addHeader("Content-Type", "application/json;charset=UTF-8")
            .build()
    }
}