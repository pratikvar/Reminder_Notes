package com.skybase.remindernotes.global

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.skybase.remindernotes.R
import com.skybase.remindernotes.repository.NoteRepository
import com.skybase.remindernotes.view.NoteActivity

object NotificationCenter {

    fun sendNoteReminderNotification(context: Context, noteId: Int) {
        val note = NoteRepository.getNoteModelForId(noteId) ?: return

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = setupNotificationChannel(context, notificationManager)

        val intent = Intent(context, NoteActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent =
            PendingIntent.getActivity(context, noteId, intent, PendingIntent.FLAG_ONE_SHOT)

        val notification = NotificationCompat.Builder(context, notificationChannel)
            .setSmallIcon(R.drawable.ic_reminder_notification_icon)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.ic_reminder_icon
                )
            )
            .setContentTitle(note.title)
            .setContentText(note.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(note.body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
//            .addAction(getSnoozedNotificationIcon())

        notificationManager.notify(noteId, notification.build())
    }

//    private fun getSnoozedNotificationIcon(context: Context): NotificationCompat.Action {
////        val snoozePendingIntent=PendingIntent.getBroadcast(context,)
//        val action= NotificationCompat.Action(android.R.drawable.ic_menu_close_clear_cancel,"Snoozed")
//    }


    private fun setupNotificationChannel(
        context: Context,
        notificationManager: NotificationManager
    ): String {
        val notificationChannelId =
            context.resources.getString(R.string.notification_channel_id_reminder)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                notificationChannelId,
                context.resources.getString(R.string.notification_channel_name_reminder),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        return notificationChannelId
    }
}
