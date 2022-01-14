package com.android.code.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.android.code.R
import timber.log.Timber

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {

    @LayoutRes
    abstract fun getLayoutResId(): Int
    private var _binding: T? = null
        private set
    protected val binding get() = _binding!!

    val requestManager by lazy {
        Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (getLayoutResId() == 0) {
            throw RuntimeException("Invalid Layout Resource ID")
        }
        _binding = DataBindingUtil.inflate(inflater, getLayoutResId(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
        setViewModelOutputs()
        setViewModelInputs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    open fun initData() {}
    open fun initView() {
        val colorValue = ContextCompat.getColor(requireActivity(), R.color.color_f5f5f5)
        colorValue.let {
            requireActivity().window.statusBarColor = it
        }
    }
    open fun setViewModelOutputs() {}
    open fun setViewModelInputs() {}

    @Suppress("DEPRECATION")
    fun enableFullScreen(isEnabled: Boolean) {
        if (isEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity?.window?.insetsController?.hide(WindowInsets.Type.statusBars())
            } else {
                activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity?.window?.insetsController?.show(WindowInsets.Type.statusBars())
            } else {
                activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
    }

    protected fun showNetworkError(throwable: Throwable) {
        Timber.e(throwable)

        Toast.makeText(
            requireContext(),
            throwable.message ?: getString(R.string.common_network_error),
            Toast.LENGTH_SHORT
        ).show()
    }
}