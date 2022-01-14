package com.android.code.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.code.R
import com.android.code.util.toDp

class CommonSwipeRefreshLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    SwipeRefreshLayout(context, attrs) {

    init {
        setProgressViewOffset(true, 0, 50.toDp(context).toInt())
        setColorSchemeResources(R.color.color_3746ff)
    }
}

@BindingAdapter("refreshListener")
fun setOnRefreshListener(
    refreshLayout: SwipeRefreshLayout,
    refreshListener: RefreshListener
) {
    refreshLayout.setOnRefreshListener {
        refreshListener.refresh(refreshLayout)
    }
}

interface RefreshListener {

    fun refresh(view: SwipeRefreshLayout)
}