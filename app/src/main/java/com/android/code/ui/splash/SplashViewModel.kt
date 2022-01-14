package com.android.code.ui.splash

import com.android.code.ui.BaseViewModel

class SplashViewModel : BaseViewModel(),
    SplashInputs, SplashOutputs {

    val inPuts: SplashInputs = this
    val outPuts: SplashOutputs = this

}

interface SplashInputs {
}

interface SplashOutputs {
}