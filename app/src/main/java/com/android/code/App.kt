package com.android.code

import android.app.Application
import com.android.code.util.FlipperModule
import com.android.code.util.StethoModule
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application() {

    companion object {
        lateinit var instance: App
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            // Timber Initialize
            Timber.uprootAll()
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    val threadName = Thread.currentThread().name
                    return "<$threadName> (${element.fileName}:${element.lineNumber})#${element.methodName} "
                }
            })
        }

        StethoModule.initializeWithDefaults(this)
        FlipperModule.initialize(this)
    }
}