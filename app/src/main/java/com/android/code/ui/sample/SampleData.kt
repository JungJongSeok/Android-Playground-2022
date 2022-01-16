package com.android.code.ui.sample

import com.android.code.models.marvel.Result

sealed class SampleData(open val result: Result)

data class SampleLeftData(
    override val result: Result
) : SampleData(result)

data class SampleRightData(
    override val result: Result
) : SampleData(result)