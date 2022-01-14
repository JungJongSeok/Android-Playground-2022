package com.android.code.binding

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.android.code.ui.views.ZoomEffectView

object ViewBinding {

    @JvmStatic
    @BindingAdapter("loadImage")
    fun bindLoadImage(imageView: ImageView, url: String?) {
        url ?: return

        Glide.with(imageView.context).load(url).into(imageView)
    }

    @JvmStatic
    @BindingAdapter("loadImage", "requestManager")
    fun bindLoadImage(imageView: ImageView, url: String?, requestManager: RequestManager?) {
        url ?: return
        requestManager ?: return

        requestManager.load(url).into(imageView)
    }

    @JvmStatic
    @BindingAdapter("zoomImage")
    fun zoomImage(zoomEffectView: ZoomEffectView, view: View) {
        zoomEffectView.zoomView = view
    }
}
