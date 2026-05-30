package com.skybase.remindernotes.global.util

import com.skybase.remindernotes.global.AlarmScheduler
import java.util.Calendar

object DateTimeUtil {

    suspend fun setAlarmForReminder(timeMillis: Long, noteId: Int): Boolean {
        return AlarmScheduler.scheduleReminder(timeMillis, noteId)
    }
}

fun Calendar.addField(field: Int, amount: Int): Calendar {
    add(field, amount)
    return this
}

fun Calendar.setFiled(field: Int, amount: Int): Calendar {
    set(field, amount)
    return this
}

fun Calendar.setTimeMillis(amount: Long): Calendar {
    timeInMillis = amount
    return this
}
