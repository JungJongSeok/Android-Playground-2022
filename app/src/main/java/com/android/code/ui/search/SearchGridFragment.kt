package com.android.code.ui.search

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.code.R
import com.android.code.databinding.FragmentSearchGridBinding
import com.android.code.ui.BaseFragment
import com.android.code.ui.main.MainActivity
import com.android.code.ui.main.MainViewModel
import com.android.code.ui.views.CommonSwipeRefreshLayout
import com.android.code.util.empty
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchGridFragment : BaseFragment<FragmentSearchGridBinding>(),
    CommonSwipeRefreshLayout.OnRefreshListener {
    companion object {
        fun newInstance() = SearchGridFragment()

        private const val GRID_SPAN_COUNT = 2
    }

    private val viewModel: SearchBaseViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val adapter by lazy {
        SearchAdapter(object : SearchAdapterProperty {
            override val requestManager: RequestManager
                get() = this@SearchGridFragment.requestManager
            override val searchedData: LiveData<SearchBaseData>
                get() = viewModel.searchedData
            override val searchedText: LiveData<String>
                get() = viewModel.searchedText

            override fun search(text: String) {
                binding.parent.searchView.setQuery(text, false)
            }

            override fun removeRecentSearch(text: String) {
                viewModel.inputs.removeRecentSearch(text)
            }

            override fun clickData(searchData: SearchData) {
                viewModel.inputs.clickData(searchData)
                mainViewModel.inputs.clickData(searchData)
            }
        })
    }

    private val layoutManager by lazy {
        GridLayoutManager(requireContext(), GRID_SPAN_COUNT).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (adapter.currentList.getOrNull(position) is SearchRecentData) {
                        GRID_SPAN_COUNT
                    } else {
                        1
                    }
                }
            }
        }
    }

    override fun getLayoutResId(): Int = R.layout.fragment_search_grid

    override fun initView(savedInstanceState: Bundle?) {
        binding.refresh = this

        binding.parent.recyclerView.layoutManager = layoutManager
        binding.parent.recyclerView.adapter = adapter
        binding.parent.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (layoutManager.findLastVisibleItemPosition() > adapter.itemCount - 5
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
            binding.loading.parent.isVisible = it
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

        mainViewModel.outputs.scrollToTop.observe(this) {
            if (it != MainActivity.PAGE_GRID) {
                return@observe
            }
            binding.parent.recyclerView.stopScroll()
            layoutManager.scrollToPositionWithOffset(0, 0)
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