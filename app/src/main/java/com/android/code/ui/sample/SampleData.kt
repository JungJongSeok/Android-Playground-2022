package com.android.code.ui.sample

import com.android.code.models.marvel.Result

sealed class SampleData(val result: Result)

data class SampleLeftData(
    private val _result: Result
) : SampleData(_result)

data class SampleRightData(
    private val _result: Result
) : SampleData(_result)