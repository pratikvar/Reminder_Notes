package com.skybase.remindernotes.global.util

import android.app.PendingIntent
import android.os.Build

object PendingIntentUtil {

    fun activityFlags(oneShot: Boolean = false): Int {
        val base = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }
        return base or if (oneShot) PendingIntent.FLAG_ONE_SHOT else PendingIntent.FLAG_UPDATE_CURRENT
    }

    fun broadcastFlags(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    }
}
