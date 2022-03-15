package com.android.code.lib.hilt

import android.app.Application
import com.android.code.lib.network.MarvelService
import com.android.code.lib.network.provideAPIClientService
import com.android.code.repository.MarvelRepositoryImpl
import com.android.code.repository.MarvelRxRepositoryImpl
import com.android.code.ui.main.MainViewModel
import com.android.code.ui.search.SearchBaseViewModel
import com.android.code.ui.search.SearchRxBaseViewModel
import com.android.code.ui.search.SearchType
import com.android.code.util.SharedPreferencesManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
class ViewModelModule {
    @Provides
    fun provideMainViewMode() = MainViewModel()

    @Provides
    fun provideSearchBaseViewModel(marvelRepository: MarvelRepositoryImpl) =
        SearchBaseViewModel(marvelRepository)

    @Provides
    fun provideSearchRxBaseViewModel(marvelRxRepository: MarvelRxRepositoryImpl) =
        SearchRxBaseViewModel(marvelRxRepository)
}


@Module
@InstallIn(ActivityRetainedComponent::class)
class RepositoryModule {
    @Provides
    fun provideMarvelRepository(
        marvelService: MarvelService,
        sharedPreferencesManager: SharedPreferencesManagerImpl,
    ) = MarvelRepositoryImpl(marvelService, sharedPreferencesManager, SearchType.GRID)

    @Provides
    fun provideMarvelRxRepository(
        marvelService: MarvelService,
        sharedPreferencesManager: SharedPreferencesManagerImpl,
    ) = MarvelRxRepositoryImpl(marvelService, sharedPreferencesManager, SearchType.STAGGERED)
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideAPIClientService() = provideAPIClientService<MarvelService>()
}

@Module
@InstallIn(ActivityComponent::class)
class ManagerModule {
    @Provides
    fun provideSharedPreferencesManager(@ApplicationContext app: Application) =
        SharedPreferencesManagerImpl(app)
}