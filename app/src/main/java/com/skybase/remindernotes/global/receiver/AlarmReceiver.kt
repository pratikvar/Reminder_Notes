package com.skybase.remindernotes.global.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.skybase.remindernotes.R
import com.skybase.remindernotes.global.NotificationCenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val action = context.resources.getString(R.string.note_reminder_triggered)
                if (intent.action == action) {
                    val noteId = intent.getIntExtra(
                        context.getString(R.string.intent_extra_id),
                        0
                    )
                    if (noteId != 0) {
                        NotificationCenter.sendNoteReminderNotification(context, noteId)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
