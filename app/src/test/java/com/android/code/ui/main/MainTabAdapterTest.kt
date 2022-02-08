package com.android.code.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock


internal class MainTabAdapterTest {
    private lateinit var mainTabAdapter: MainTabAdapter
    private lateinit var fragments: List<Fragment>

    @BeforeEach
    fun setUp() {
        val fragment1: Fragment = mock {
            on { id } doReturn 1
            on { tag } doReturn "THREE"
        }
        val fragment2: Fragment = mock {
            on { id } doReturn 1
            on { tag } doReturn "THREE"
        }
        val fragment3: Fragment = mock {
            on { id } doReturn 1
            on { tag } doReturn "THREE"
        }
        fragments = listOf(fragment1, fragment2, fragment3)
        val lifecycle: Lifecycle = mock { }
        val fragmentManager: FragmentManager = mock { }
        val fragmentActivity: FragmentActivity = mock {
            on { this.lifecycle } doReturn lifecycle
            on { this.supportFragmentManager } doReturn fragmentManager
            on {}
        }
        mainTabAdapter = mock {
            on { this.currentList } doReturn fragments
        }
    }

    @Test
    fun getItemCount() {
        assertEquals(mainTabAdapter.itemCount, fragments.size)
    }

    @Test
    fun createFragment() {
        fragments.forEachIndexed { index, fragment ->
            assertEquals(mainTabAdapter.createFragment(index), fragment)
        }
    }
}