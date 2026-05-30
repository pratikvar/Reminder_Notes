package com.skybase.remindernotes.global.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.skybase.remindernotes.global.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        if (!shouldReschedule(intent.action)) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                AlarmScheduler.rescheduleAllPendingReminders()
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun shouldReschedule(action: String?): Boolean {
        return when (action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            ACTION_QUICKBOOT_POWERON -> true
            else -> false
        }
    }

    companion object {
        private const val ACTION_QUICKBOOT_POWERON = "android.intent.action.QUICKBOOT_POWERON"
    }
}
