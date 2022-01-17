package com.android.code.ui.main

import com.android.code.ui.BaseViewModel

class MainViewModel : BaseViewModel(),
    MainViewModelInput, MainViewModelOutput {

    val inputs: MainViewModelInput = this
    val outputs: MainViewModelOutput = this

}

interface MainViewModelInput {
    // Do something
}

interface MainViewModelOutput {
    // Do something
}