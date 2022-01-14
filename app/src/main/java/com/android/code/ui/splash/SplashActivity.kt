package com.android.code.ui.splash

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import com.karumi.dexter.Dexter
import com.android.code.R
import com.android.code.databinding.ActivitySplashBinding
import com.android.code.ui.BaseActivity
import com.android.code.ui.RequiresActivityViewModel
import com.android.code.ui.sample.SampleActivity
import com.android.code.util.withCustomListener
import timber.log.Timber

@RequiresActivityViewModel(value = SplashViewModel::class)
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {

    @LayoutRes
    override fun getLayoutResId() = R.layout.activity_splash

    override fun initData() {
        checkGrantPermission()
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    override fun setViewModelOutputs() {
        viewModel.error.observe(this) {
            showNetworkError(it)
        }
    }

    override fun setViewModelInputs() {
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkGrantPermission() {
        val permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val denied = permissions.find { checkSelfPermission(it) != PERMISSION_GRANTED }
        val nextProcess = {
            SampleActivity.startActivity(this)
        }

        if (denied != null) {
            Dexter.withContext(this)
                .withPermissions(permissions)
                .withCustomListener(
                    this,
                    onSuccess = {
                        nextProcess()
                    },
                    onDenied = { deniedResponses ->
                        Timber.e("base android app: denied permission -> [${deniedResponses.joinToString { it.permissionName }}]")
                    }
                )
                .withErrorListener {
                    Timber.e("base android app: ${it.name}, $it")

                }
                .check()
        } else {
            nextProcess()
        }
    }
}