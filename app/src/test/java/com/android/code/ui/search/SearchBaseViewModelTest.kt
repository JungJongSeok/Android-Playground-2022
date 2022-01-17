package com.android.code.ui.search

import com.android.code.CoroutinesTestExtension
import com.android.code.InstantExecutorExtension
import com.android.code.getOrAwaitValue
import com.android.code.models.BaseResponse
import com.android.code.models.marvel.MarvelResult
import com.android.code.models.marvel.SampleResponse
import com.android.code.models.repository.MarvelRepository
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.system.measureTimeMillis

@kotlinx.coroutines.ExperimentalCoroutinesApi
@DisplayName("SearchBaseViewModel 테스트")
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
internal class SearchBaseViewModelTest {
    private lateinit var searchBaseViewModel: SearchBaseViewModel

    @BeforeEach
    fun setUp() {
        val marvelResult: MarvelResult = mock {
            on { id } doReturn 1
        }
        val sampleResponse: SampleResponse = mock {
            on { count } doReturn 20
            on { total } doReturn 1000
            on { results } doReturn listOf(marvelResult, marvelResult, marvelResult)
        }
        val marvelRepository: MarvelRepository = object : MarvelRepository {
            override suspend fun characters(
                nameStartsWith: String?,
                offset: Int,
                limit: Int
            ): BaseResponse<SampleResponse> {
                return BaseResponse(sampleResponse)
            }

            override var recentGridSearchList: List<String>? = listOf("GRID")
            override var recentStaggeredSearchList: List<String>? = listOf("STAGGERED", "STAGGERED")

        }
        searchBaseViewModel = object : SearchBaseViewModel(marvelRepository) {
            override var preferencesRecentSearchList: List<String>? = listOf("123", "456", "789")
        }
    }

    @Test
    fun initData() {
        runBlocking {
            val totalExecutionTime = measureTimeMillis {
                searchBaseViewModel.initData()
                assertEquals(searchBaseViewModel.responseData.getOrAwaitValue().first.size, 4)
            }

            println("initData() Total Time: $totalExecutionTime")
        }
    }

    @Test
    fun search() {
        runBlocking {
            val totalExecutionTime = measureTimeMillis {
                searchBaseViewModel.search("spi")
                assertEquals(searchBaseViewModel.searchedText.getOrAwaitValue(), "spi")
                searchBaseViewModel.search("spider-man")
                assertEquals(searchBaseViewModel.searchedText.getOrAwaitValue(), "spi")
                delay(1000)
                searchBaseViewModel.search("spider-man")
                assertEquals(searchBaseViewModel.searchedText.getOrAwaitValue(), "spider-man")
            }

            println("search() Total Time: $totalExecutionTime")
        }
    }

    @Test
    fun searchMore() {
        runBlocking {
            launch {
                val totalExecutionTime = measureTimeMillis {
                    searchBaseViewModel.initData()
                    assertEquals(searchBaseViewModel.responseData.getOrAwaitValue().first.size, 4)
                    delay(500)
                    searchBaseViewModel.searchMore()
                    assertEquals(searchBaseViewModel.responseData.getOrAwaitValue().first.size, 7)
                }

                println("searchMore() Total Time: $totalExecutionTime")
            }
        }
    }

    @Test
    fun canSearchMore() {
        runBlocking {
            val totalExecutionTime = measureTimeMillis {
                assertEquals(searchBaseViewModel.canSearchMore(), false)
                searchBaseViewModel.initData()
                assertEquals(searchBaseViewModel.canSearchMore(), true)
            }

            println("removeRecentSearch() Total Time: $totalExecutionTime")
        }
    }

    @Test
    fun removeRecentSearch() {
        runBlocking {
            val totalExecutionTime = measureTimeMillis {
                searchBaseViewModel.removeRecentSearch("123")
                assertEquals(searchBaseViewModel.preferencesRecentSearchList?.size, 2)
            }

            println("removeRecentSearch() Total Time: $totalExecutionTime")
        }
    }

    @Test
    fun clickData() {
        runBlocking {
            val searchData = mock<SearchData>()
            val totalExecutionTime = measureTimeMillis {
                searchBaseViewModel.clickData(searchData)
                assertEquals(searchBaseViewModel.clickData.getOrAwaitValue(), searchData)
            }

            println("clickData() Total Time: $totalExecutionTime")
        }
    }
}