package com.android.code.ui.sample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.android.code.databinding.HolderSampleLeftBinding
import com.android.code.databinding.HolderSampleRightBinding
import com.android.code.util.ViewDetectable

class SampleAdapter(
    private val viewModel: SampleViewModel,
    private val requestManager: RequestManager
) :
    ListAdapter<SampleData, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<SampleData>() {
            override fun areContentsTheSame(oldItem: SampleData, newItem: SampleData): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: SampleData, newItem: SampleData): Boolean {
                return true
            }
        }
    ) {
    companion object {
        private const val TYPE_LEFT = 0
        private const val TYPE_RIGHT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SampleLeftData -> TYPE_LEFT
            is SampleRightData -> TYPE_RIGHT
            else -> throw IllegalArgumentException("Do not define Type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_LEFT -> SampleLeftHolder(
                HolderSampleLeftBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            TYPE_RIGHT -> SampleRightHolder(
                HolderSampleRightBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
            else -> throw IllegalArgumentException("Do not define Type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SampleLeftHolder -> {
                holder.binding.apply {
                    viewModel = this@SampleAdapter.viewModel
                    requestManager = this@SampleAdapter.requestManager
                    data = getItem(position) as? SampleLeftData
                    executePendingBindings()
                }
            }
            is SampleRightHolder -> {
                holder.binding.apply {
                    viewModel = this@SampleAdapter.viewModel
                    requestManager = this@SampleAdapter.requestManager
                    data = getItem(position) as? SampleRightData
                    executePendingBindings()
                }
            }

        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder is ViewDetectable) {
            holder.onViewAttachedToWindow(holder)
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is ViewDetectable) {
            holder.onViewDetachedFromWindow(holder)
        }
    }

    private inner class SampleLeftHolder(val binding: HolderSampleLeftBinding) :
        RecyclerView.ViewHolder(binding.root), ViewDetectable {

        private val toggleObservers = Observer<SampleData> {
            binding.switchButton.isChecked = it.result.id == binding.data?.result?.id ?: false
        }

        override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
            viewModel.outputs.toggle.observeForever(toggleObservers)
        }

        override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
            viewModel.outputs.toggle.removeObserver(toggleObservers)
        }
    }

    private inner class SampleRightHolder(val binding: HolderSampleRightBinding) :
        RecyclerView.ViewHolder(binding.root), ViewDetectable {

        private val toggleObservers = Observer<SampleData> {
            binding.switchButton.isChecked = it.result.id == binding.data?.result?.id ?: false
        }

        override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
            viewModel.outputs.toggle.observeForever(toggleObservers)
        }

        override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
            viewModel.outputs.toggle.removeObserver(toggleObservers)
        }
    }
}