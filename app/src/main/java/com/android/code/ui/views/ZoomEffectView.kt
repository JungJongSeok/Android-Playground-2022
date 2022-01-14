package com.android.code.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.cardview.widget.CardView
import com.android.code.R

@SuppressLint("CustomViewStyleable", "ClickableViewAccessibility")
class ZoomEffectView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private var zoomPercent: Float = 0f
    private var zoomDuration: Int = 0

    var zoomView: View? = null

    init {
        isClickable = true

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZoomEffectLayout)

        typedArray.apply {
            zoomPercent = getFloat(R.styleable.ZoomEffectLayout_zoomPercent, 1.1F)
            zoomDuration = getInt(
                R.styleable.ZoomEffectLayout_zoomDuration,
                resources.getInteger(android.R.integer.config_shortAnimTime)
            )
            recycle()
        }

        setOnTouchListener { _, event ->
            val view = zoomView ?: return@setOnTouchListener false
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.animate().scaleX(zoomPercent).scaleY(zoomPercent)
                        .setDuration(zoomDuration.toLong()).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    view.animate().scaleX(1.0F).scaleY(1.0F)
                        .setDuration(zoomDuration.toLong()).start()
                }

            }
            false
        }
    }
}