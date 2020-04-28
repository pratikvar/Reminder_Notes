package com.skybase.remindernotes.global.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.skybase.remindernotes.R
import com.skybase.remindernotes.global.NoteApplication
import com.skybase.remindernotes.global.receiver.AlarmReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

object DateTimeUtil {

    suspend fun setAlarmForReminder(timeMillis: Long, noteId: Int) {
        withContext(Dispatchers.Main) {
            val cal = Calendar.getInstance()
                .setTimeMillis(timeMillis)
                .setFiled(Calendar.SECOND, 0)
                .setFiled(Calendar.MILLISECOND, 0)

            val context = NoteApplication.getApplicationInstance()
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent: Intent = Intent(context, AlarmReceiver::class.java)
            intent.action = context.resources!!.getString(R.string.note_reminder_triggered)
            intent.putExtra(context.getString(R.string.intent_extra_id), noteId)
            val pendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    noteId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
        }
    }
}

fun Calendar.addField(field: Int, amount: Int): Calendar {
    this.add(field, amount)
    return this
}

fun Calendar.setFiled(field: Int, amount: Int): Calendar {
    this.set(field, amount)
    return this
}

fun Calendar.setTimeMillis(amount: Long): Calendar {
    this.timeInMillis = amount
    return this
}
