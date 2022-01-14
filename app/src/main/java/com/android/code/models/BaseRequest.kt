package com.android.code.models

import com.google.gson.annotations.SerializedName

data class BaseRequest<out T>(
    @SerializedName("data")
    val data: T
)
