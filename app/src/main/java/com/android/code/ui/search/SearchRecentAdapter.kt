package com.android.code.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.code.databinding.HolderSearchRecentDataBinding

class SearchRecentAdapter(private val property: SearchRecentAdapterProperty) :
    ListAdapter<String, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<String>() {
            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return true
            }
        }
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchRecentHolder(
            HolderSearchRecentDataBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SearchRecentHolder -> {
                holder.binding.apply {
                    val data = getItem(position)
                    property = this@SearchRecentAdapter.property
                    text = data
                    isSelected = data == this@SearchRecentAdapter.property.searchedText.value
                    executePendingBindings()
                }
            }
        }
    }

    private inner class SearchRecentHolder(val binding: HolderSearchRecentDataBinding) :
        RecyclerView.ViewHolder(binding.root)

}