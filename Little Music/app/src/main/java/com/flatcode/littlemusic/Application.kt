package com.flatcode.littlemusic

import android.app.Application
import android.text.format.DateFormat
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import dagger.hilt.android.HiltAndroidApp
import io.selimdawa.multicolors.MultiColorManager
import timber.log.Timber
import com.flatcode.littlemusic.BuildConfig
import com.flatcode.littlemusic.utils.DATA
import java.util.Calendar
import java.util.Locale

@HiltAndroidApp
class Application : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
        MultiColorManager.init(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    // Cloudinary Initialization
    val config = mapOf(
        "cloud_name" to DATA.CLOUDINARY_CLOUD_NAME, "secure" to true
    )
    MediaManager.init(this, config)

    override fun newImageLoader(context: coil3.PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(OkHttpNetworkFetcherFactory())
            }
            .build()
    }

    companion object {
        fun formatTimestamp(timestamp: Long): String {
            val calendar = Calendar.getInstance(Locale.ENGLISH)
            calendar.timeInMillis = timestamp
            return DateFormat.format("dd/MM/yyyy", calendar).toString()
        }
    }
}