package com.skybase.remindernotes.global

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.skybase.remindernotes.R
import com.skybase.remindernotes.global.receiver.AlarmReceiver
import com.skybase.remindernotes.global.util.PendingIntentUtil
import com.skybase.remindernotes.global.util.PermissionHelper
import com.skybase.remindernotes.global.util.setFiled
import com.skybase.remindernotes.global.util.setTimeMillis
import com.skybase.remindernotes.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

object AlarmScheduler {

    /**
     * @return true if an exact alarm was scheduled, false if skipped (past time, or permission denied).
     */
    suspend fun scheduleReminder(timeMillis: Long, noteId: Int): Boolean {
        return withContext(Dispatchers.Main) {
            val triggerAt = normalizeTriggerTime(timeMillis)
            if (triggerAt <= System.currentTimeMillis()) return@withContext false

            val context = NoteApplication.getApplicationInstance()
            if (!PermissionHelper.canScheduleExactAlarms(context)) {
                return@withContext false
            }

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = createReminderPendingIntent(context, noteId)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAt,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            }
            true
        }
    }

    suspend fun rescheduleAllPendingReminders() {
        val now = System.currentTimeMillis()
        val notes = withContext(Dispatchers.IO) {
            NoteRepository.getNotesWithFutureReminders(now)
        }
        for (note in notes) {
            val noteId = note.Id ?: continue
            val reminder = note.Reminder ?: continue
            scheduleReminder(reminder, noteId)
        }
    }

    fun cancelReminder(noteId: Int) {
        val context = NoteApplication.getApplicationInstance()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(createReminderPendingIntent(context, noteId))
    }

    private fun normalizeTriggerTime(timeMillis: Long): Long {
        return Calendar.getInstance()
            .setTimeMillis(timeMillis)
            .setFiled(Calendar.SECOND, 0)
            .setFiled(Calendar.MILLISECOND, 0)
            .timeInMillis
    }

    private fun createReminderPendingIntent(context: Context, noteId: Int): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = context.getString(R.string.note_reminder_triggered)
            putExtra(context.getString(R.string.intent_extra_id), noteId)
        }
        return PendingIntent.getBroadcast(
            context,
            noteId,
            intent,
            PendingIntentUtil.broadcastFlags()
        )
    }
}
