package com.android.code.lib.koin

import com.android.code.lib.network.MarvelService
import com.android.code.repository.MarvelRepository
import com.android.code.repository.MarvelRepositoryImpl
import com.android.code.ui.main.MainViewModel
import com.android.code.ui.search.SearchGridViewModel
import com.android.code.ui.search.SearchStaggeredViewModel
import com.android.code.util.SharedPreferencesManager
import com.android.code.util.SharedPreferencesManagerImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val uiModule = module {
    viewModel { MainViewModel() }
    viewModel { SearchGridViewModel(get()) }
    viewModel { SearchStaggeredViewModel(get()) }
}

val repositoryModule = module {
    factory<MarvelRepository> { MarvelRepositoryImpl(get(), get()) }
}

val networkModule = module {
    single { provideAPIClientService<MarvelService>() }
}

val managerModule = module {
    factory<SharedPreferencesManager> { SharedPreferencesManagerImpl(androidApplication()) }
}