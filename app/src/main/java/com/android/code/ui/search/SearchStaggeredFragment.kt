package com.android.code.ui.search

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.code.R
import com.android.code.databinding.FragmentSearchStaggeredBinding
import com.android.code.ui.BaseFragment
import com.android.code.ui.views.OnRefreshListener
import com.android.code.util.empty
import com.bumptech.glide.RequestManager
import org.koin.android.ext.android.inject

class SearchStaggeredFragment : BaseFragment<FragmentSearchStaggeredBinding>(), OnRefreshListener {
    companion object {
        fun newInstance() = SearchStaggeredFragment()

        private const val GRID_SPAN_COUNT = 2
    }

    private val viewModel: SearchStaggeredViewModel by inject()

    private val adapter by lazy {
        SearchAdapter(object : SearchAdapterProperty {
            override val requestManager: RequestManager
                get() = this@SearchStaggeredFragment.requestManager
            override val searchedData: LiveData<SearchBaseData>
                get() = viewModel.searchedData
            override val searchedText: LiveData<String>
                get() = viewModel.searchedText

            override fun search(text: String) {
                binding.parent.searchView.setQuery(text, false)
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
        StaggeredGridLayoutManager(GRID_SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        }
    }

    override fun getLayoutResId(): Int = R.layout.fragment_search_staggered

    override fun initView(savedInstanceState: Bundle?) {
        binding.refresh = this

        binding.parent.recyclerView.layoutManager = layoutManager
        binding.parent.recyclerView.adapter = adapter
        binding.parent.recyclerView.itemAnimator = null
        binding.parent.recyclerView.clearOnScrollListeners()
        binding.parent.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (layoutManager.findLastVisibleItemPositions(null)
                        .lastOrNull() ?: return > adapter.itemCount - 5
                    && viewModel.canSearchMore()
                ) {
                    viewModel.inputs.searchMore()
                }
            }
        })

        binding.parent.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.search(newText ?: String.empty())
                return true
            }

        })

        if (savedInstanceState != null) {
            binding.parent.searchView.setQuery(String.empty(), false)
        }
    }

    override fun setViewModelOutputs() {
        viewModel.loading.observe(this) {
            if (it) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        }

        viewModel.error.observe(this) {
            showNetworkError(it)
        }

        viewModel.outputs.responseData.observe(this) { (list, isScrolled) ->
            adapter.submitList(list) {
                adapter.notifyDataSetChanged()
                if (isScrolled) {
                    layoutManager.scrollToPositionWithOffset(0, 0)
                }
            }
        }

        viewModel.outputs.clickData.observe(this) {
            if (it !is SearchBaseData) {
                return@observe
            }
            Toast.makeText(requireContext(), it.result.name, Toast.LENGTH_SHORT).show()
        }

        viewModel.outputs.searchedText.observe(this) {
            binding.parent.searchView.setQuery(it, false)
        }

        viewModel.outputs.refreshedSwipeRefreshLayout.observe(this) {
            binding.parent.refreshLayout.isRefreshing = it
        }
    }

    override fun setViewModelInputs() {
        viewModel.inputs.initData()
    }

    override fun refresh(view: SwipeRefreshLayout) {
        if (binding.parent.searchView.query.isNullOrEmpty()) {
            viewModel.inputs.initData(true)
        } else {
            viewModel.inputs.search(binding.parent.searchView.query.toString(), true)
        }
    }
}