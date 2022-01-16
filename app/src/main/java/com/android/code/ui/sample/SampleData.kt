package com.android.code.ui.sample

import com.android.code.models.marvel.MarvelResult

sealed class SampleData(open val result: MarvelResult)

data class SampleLeftData(
    override val result: MarvelResult
) : SampleData(result)

data class SampleRightData(
    override val result: MarvelResult
) : SampleData(result)