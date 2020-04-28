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

        CoroutineScope(Dispatchers.Default).launch {
            if (intent != null && intent.action.equals(context?.resources!!.getString(R.string.note_reminder_triggered))) {
                val noteId =
                    intent.getIntExtra(context.resources.getString(R.string.intent_extra_id), 0)

                NotificationCenter.sendNoteReminderNotification(context, noteId)
            }
        }
    }
}