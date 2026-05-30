package com.skybase.remindernotes.global.util

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat

object SystemBarHelper {

    /** Dark status/navigation icons � use on light backgrounds (notes list, editor). */
    fun enableEdgeToEdgeWithLightBackground(activity: ComponentActivity) {
        activity.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        applyLightSystemBarIcons(activity)
    }

    /** Light status/navigation icons � use on dark backgrounds (splash). */
    fun enableEdgeToEdgeWithDarkBackground(activity: ComponentActivity) {
        activity.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        applyDarkSystemBarIcons(activity)
    }

    private fun applyLightSystemBarIcons(activity: ComponentActivity) {
        WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
    }

    private fun applyDarkSystemBarIcons(activity: ComponentActivity) {
        WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }
}
