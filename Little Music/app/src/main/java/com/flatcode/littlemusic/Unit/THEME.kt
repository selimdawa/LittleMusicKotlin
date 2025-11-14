package com.flatcode.littlemusic.Unitimport

import android.content.*
import androidx.preference.PreferenceManager
import com.flatcode.littlemusic.R

object THEME {
    fun setThemeOfApp(context: Context) {
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(context.applicationContext)
        if (sharedPreferences.getString("color_option", "ONE") == "ONE") {
            context.setTheme(R.style.OneTheme)
        } else if (sharedPreferences.getString("color_option", "NIGHT_ONE") == "NIGHT_ONE") {
            context.setTheme(R.style.OneNightTheme)
        }
    }
}