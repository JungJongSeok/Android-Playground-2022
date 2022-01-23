package com.android.code.ui.main

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainTabAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val _currentList = mutableListOf<Fragment>()
    val currentList: List<Fragment>
        get() = _currentList

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<Fragment>) {
        _currentList.clear()
        _currentList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return _currentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return _currentList[position]
    }
}