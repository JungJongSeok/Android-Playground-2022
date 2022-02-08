package com.android.code.lib.koin

import com.android.code.lib.network.MarvelService
import com.android.code.lib.network.provideAPIClientService
import com.android.code.repository.MarvelRepository
import com.android.code.repository.MarvelRepositoryImpl
import com.android.code.repository.MarvelRxRepository
import com.android.code.repository.MarvelRxRepositoryImpl
import com.android.code.ui.main.MainViewModel
import com.android.code.ui.search.SearchBaseViewModel
import com.android.code.ui.search.SearchRxBaseViewModel
import com.android.code.ui.search.SearchType
import com.android.code.util.SharedPreferencesManager
import com.android.code.util.SharedPreferencesManagerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val uiModule = module {
    viewModel { MainViewModel() }
    viewModel { (type: SearchType) -> SearchBaseViewModel(get { parametersOf(type) }) }
    viewModel { (type: SearchType) -> SearchRxBaseViewModel(get { parametersOf(type) }) }
}

val repositoryModule = module {
    factory<MarvelRepository> { (type: SearchType) -> MarvelRepositoryImpl(get(), get(), type) }
    factory<MarvelRxRepository> { (type: SearchType) -> MarvelRxRepositoryImpl(get(), get(), type) }
}

val networkModule = module {
    single { provideAPIClientService<MarvelService>() }
}

val managerModule = module {
    factory<SharedPreferencesManager> { SharedPreferencesManagerImpl(androidContext()) }
}