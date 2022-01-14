package com.android.code.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener

fun DexterBuilder.MultiPermissionListener.withCustomListener(
    context: Context,
    onSuccess: () -> Unit,
    onDenied: (List<PermissionDeniedResponse>) -> Unit
): DexterBuilder {
    return this.withListener(object : MultiplePermissionsListener {
        override fun onPermissionsChecked(
            report: MultiplePermissionsReport?,
        ) {
            report?.let {
                when {
                    it.areAllPermissionsGranted() -> onSuccess.invoke()
                    it.isAnyPermissionPermanentlyDenied -> {
                        onDenied.invoke(it.deniedPermissionResponses)
                        context.openSettings()
                    }
                    else -> onDenied.invoke(it.deniedPermissionResponses)
                }
            }
        }

        override fun onPermissionRationaleShouldBeShown(
            permissions: MutableList<PermissionRequest>?,
            token: PermissionToken?,
        ) {
            token?.continuePermissionRequest()
        }
    })
}

fun DexterBuilder.SinglePermissionListener.withCustomListener(
    context: Context,
    onSuccess: () -> Unit,
    onDenied: (PermissionDeniedResponse?) -> Unit
): DexterBuilder {
    return this.withListener(object : PermissionListener {
        override fun onPermissionGranted(response: PermissionGrantedResponse?) {
            onSuccess.invoke()
        }

        override fun onPermissionDenied(response: PermissionDeniedResponse?) {
            if (response?.isPermanentlyDenied == true) {
                context.openSettings()
            }
            onDenied.invoke(response)
        }

        override fun onPermissionRationaleShouldBeShown(
            permission: PermissionRequest?,
            token: PermissionToken?
        ) {
            token?.continuePermissionRequest()
        }
    })
}


// 어플리케이션 정보 설정 페이지
private fun Context.openSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri: Uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}