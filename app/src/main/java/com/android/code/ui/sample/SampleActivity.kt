package com.android.code.ui.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.code.R
import com.android.code.databinding.ActivitySampleBinding
import com.android.code.ui.BaseActivity
import com.android.code.ui.RequiresActivityViewModel
import com.android.code.util.empty
import com.bumptech.glide.RequestManager
import timber.log.Timber


@RequiresActivityViewModel(value = SampleViewModel::class)
class SampleActivity : BaseActivity<ActivitySampleBinding, SampleViewModel>() {
    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, SampleActivity::class.java))
        }

        private const val GRID_SPAN_COUNT = 2
    }

    private val adapter by lazy {
        SampleSearchAdapter(object : SampleSearchAdapterProperty {
            override val requestManager: RequestManager
                get() = this@SampleActivity.requestManager
            override val searchedText: LiveData<String>
                get() = viewModel.searchedText

            override fun search(text: String) {
                binding.searchView.setQuery(text, false)
            }

            override fun removeRecentSearch(text: String) {
                viewModel.removeRecentSearch(text)
            }

            override fun clickData(searchData: SearchData) {
                viewModel.clickData(searchData)
            }
        })
    }

    private val layoutManager by lazy {
        GridLayoutManager(this, GRID_SPAN_COUNT).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (adapter.currentList.getOrNull(position) is SearchRecentData) {
                        2
                    } else {
                        1
                    }
                }
            }
        }
    }

    override fun getLayoutResId(): Int = R.layout.activity_sample

    override fun initView(savedInstanceState: Bundle?) {
        binding.activity = this

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.itemAnimator = null

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Timber.e("aaaa" + newText)
                viewModel.search(newText ?: String.empty())
                return true
            }

        })
    }

    override fun setViewModelOutputs() {
        viewModel.outputs.responseData.observe(this) {
            adapter.submitList(it) {
                layoutManager.scrollToPositionWithOffset(0, 0)
            }
        }

        viewModel.outputs.clickData.observe(this) {
            if (it !is SearchBaseData) {
                return@observe
            }
            Toast.makeText(this, it.result.name, Toast.LENGTH_SHORT).show()
        }

        viewModel.outputs.searchedText.observe(this) {
            binding.searchView.setQuery(it, false)
        }
    }

    override fun setViewModelInputs() {
        viewModel.inputs.initData()
    }

    fun refresh(refreshLayout: SwipeRefreshLayout?) {
        if (binding.searchView.query.isNullOrEmpty()) {
            viewModel.inputs.initData()
        } else {
            viewModel.inputs.search(binding.searchView.query.toString())
        }
        refreshLayout?.isRefreshing = false
    }
}