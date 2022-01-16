package com.android.code.ui.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.code.R
import com.android.code.databinding.ActivitySampleBinding
import com.android.code.ui.BaseActivity
import com.android.code.ui.RequiresActivityViewModel


@RequiresActivityViewModel(value = SampleViewModel::class)
class SampleActivity : BaseActivity<ActivitySampleBinding, SampleViewModel>() {
    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, SampleActivity::class.java))
        }
    }

    private val adapter by lazy {
        SampleAdapter(viewModel, requestManager)
    }

    override fun getLayoutResId(): Int = R.layout.activity_sample

    override fun initView(savedInstanceState: Bundle?) {
        binding.activity = this

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    override fun setViewModelOutputs() {
        viewModel.outputs.responseData.observe(this) {
            adapter.submitList(it)
        }

        viewModel.outputs.clickData.observe(this) {
            Toast.makeText(this, it.result.name, Toast.LENGTH_SHORT).show()
        }
    }

    override fun setViewModelInputs() {
        viewModel.inputs.initData()
    }

    fun refresh(refreshLayout: SwipeRefreshLayout?) {
        viewModel.inputs.initData()
        refreshLayout?.isRefreshing = false
    }
}