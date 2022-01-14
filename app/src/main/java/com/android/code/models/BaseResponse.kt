package com.android.code.models

import com.google.gson.annotations.SerializedName

data class BaseResponse<out T>(
    @SerializedName("data")
    val data: T
)