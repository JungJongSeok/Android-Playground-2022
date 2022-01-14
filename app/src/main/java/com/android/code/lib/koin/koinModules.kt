package com.android.code.lib.koin

import com.android.code.lib.network.MarvelService
import com.android.code.models.repository.MarvelRepository
import com.android.code.ui.sample.SampleViewModel
import com.android.code.ui.splash.SplashViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val uiModule = module {
    viewModel { SplashViewModel() }
    viewModel { SampleViewModel(get()) }
}

val repositoryModule = module {
    factory { MarvelRepository(get()) }
}

val networkModule = module {
    single { provideAPIClientService<MarvelService>() }
}