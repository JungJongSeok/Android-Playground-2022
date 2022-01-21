package com.android.code.ui.main

import android.animation.AnimatorInflater
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.IntDef
import androidx.core.animation.doOnEnd
import com.android.code.R
import com.android.code.databinding.ActivityMainBinding
import com.android.code.ui.BaseActivity
import com.android.code.ui.RequiresActivityViewModel
import com.android.code.ui.search.SearchGridFragment
import com.android.code.ui.search.SearchStaggeredFragment
import com.google.android.material.tabs.TabLayoutMediator


@RequiresActivityViewModel(value = MainViewModel::class)
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    companion object {
        const val PAGE_GRID = 0
        const val PAGE_STAGGERED = 1

        @IntDef(PAGE_GRID, PAGE_STAGGERED)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Page

        fun startActivity(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }

    private val tabAdapter by lazy {
        MainTabAdapter(
            this, listOf(
                SearchGridFragment.newInstance(),
                SearchStaggeredFragment.newInstance()
            )
        )
    }

    private val mediator by lazy {
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            when (position) {
                PAGE_GRID -> {
                    tab.text = getString(R.string.main_tab_grid)
                }
                PAGE_STAGGERED -> {
                    tab.text = getString(R.string.main_tab_staggered)
                }
            }
        }
    }

    override fun getLayoutResId(): Int = R.layout.activity_main

    override fun initView(savedInstanceState: Bundle?) {
        binding.pager.adapter = tabAdapter

        mediator.attach()
        val selectPosition = if (binding.tabLayout.selectedTabPosition < 0) {
            PAGE_GRID
        } else {
            binding.tabLayout.selectedTabPosition
        }
        if (savedInstanceState == null) {
            setCurrentItem(selectPosition)
        }
        setSplashScreen()
    }

    @TargetApi(Build.VERSION_CODES.S)
    private fun setSplashScreen() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return
        }
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            AnimatorInflater.loadAnimator(this, R.animator.splash_screen_zoom_out).apply {
                setTarget(splashScreenView)
                doOnEnd { splashScreenView.remove() }
            }.start()
        }
    }

    private fun setCurrentItem(@Page position: Int) {
        binding.pager.setCurrentItem(position, false)
    }

    override fun setViewModelOutputs() {
        // Do something
    }

    override fun setViewModelInputs() {
        // Do something
    }
}