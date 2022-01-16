package com.android.code.lib.koin

import com.android.code.lib.network.MarvelService
import com.android.code.models.repository.MarvelRepository
import com.android.code.ui.sample.SampleViewModel
import com.android.code.util.SharedPreferencesManager
import com.android.code.util.SharedPreferencesManagerImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val uiModule = module {
    viewModel { SampleViewModel(get()) }
}

val repositoryModule = module {
    factory { MarvelRepository(get(), get()) }
}

val networkModule = module {
    single { provideAPIClientService<MarvelService>() }
}

val managerModule = module {
    factory<SharedPreferencesManager> { SharedPreferencesManagerImpl(androidApplication()) }
}