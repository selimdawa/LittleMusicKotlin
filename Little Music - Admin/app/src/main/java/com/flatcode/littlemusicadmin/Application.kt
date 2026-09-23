package com.flatcode.littlemusicadmin

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.cloudinary.android.MediaManager
import com.flatcode.littlemusicadmin.utils.DATA
import dagger.hilt.android.HiltAndroidApp
import io.selimdawa.multicolors.MultiColorManager
import timber.log.Timber

@HiltAndroidApp
class Application : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
        MultiColorManager.init(this)
        Timber.plant(Timber.DebugTree())

        // Cloudinary Initialization
        val config = mapOf(
            "cloud_name" to DATA.CLOUDINARY_CLOUD_NAME, "secure" to true
        )
        MediaManager.init(this, config)
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context).components {
            add(OkHttpNetworkFetcherFactory())
        }.build()
    }


}