package com.skybase.remindernotes.global.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatUtil {

    private val reminderFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    fun formatReminder(timestamp: Long?): String {
        if (timestamp == null) return ""
        return reminderFormat.format(Date(timestamp))
    }
}
