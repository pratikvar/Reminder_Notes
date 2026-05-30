package com.skybase.remindernotes.global.util

import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skybase.remindernotes.R

object PermissionDialogs {

    fun showExactAlarmDialog(
        activity: FragmentActivity,
        onComplete: () -> Unit = {}
    ) {
        MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.dialog_exact_alarm_title)
            .setMessage(R.string.dialog_exact_alarm_message)
            .setPositiveButton(R.string.dialog_open_settings) { _, _ ->
                PermissionHelper.openExactAlarmSettings(activity)
                onComplete()
            }
            .setNegativeButton(R.string.dialog_not_now) { _, _ -> onComplete() }
            .setOnCancelListener { onComplete() }
            .show()
    }

    fun showNotificationDialog(
        activity: FragmentActivity,
        onComplete: () -> Unit = {}
    ) {
        MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.dialog_notification_title)
            .setMessage(R.string.dialog_notification_message)
            .setPositiveButton(R.string.dialog_open_settings) { _, _ ->
                PermissionHelper.openNotificationSettings(activity)
                onComplete()
            }
            .setNegativeButton(R.string.dialog_not_now) { _, _ -> onComplete() }
            .setOnCancelListener { onComplete() }
            .show()
    }

    fun showReminderSetupDialog(
        activity: FragmentActivity,
        needsExactAlarm: Boolean,
        needsNotifications: Boolean,
        onComplete: () -> Unit = {}
    ) {
        when {
            needsExactAlarm && needsNotifications -> {
                MaterialAlertDialogBuilder(activity)
                    .setTitle(R.string.dialog_reminder_setup_title)
                    .setMessage(R.string.dialog_reminder_setup_message_both)
                    .setPositiveButton(R.string.dialog_open_settings) { _, _ ->
                        showSettingsChooserForHints(activity, needsExactAlarm = true, needsNotifications = true)
                        onComplete()
                    }
                    .setNegativeButton(R.string.dialog_not_now) { _, _ -> onComplete() }
                    .setOnCancelListener { onComplete() }
                    .show()
            }
            needsExactAlarm -> showExactAlarmDialog(activity, onComplete)
            needsNotifications -> showNotificationDialog(activity, onComplete)
            else -> onComplete()
        }
    }

    fun showSettingsChooserForHints(
        activity: FragmentActivity,
        needsExactAlarm: Boolean,
        needsNotifications: Boolean
    ) {
        when {
            needsExactAlarm && needsNotifications -> {
                MaterialAlertDialogBuilder(activity)
                    .setTitle(R.string.dialog_reminder_setup_title)
                    .setMessage(R.string.dialog_hint_open_settings_message)
                    .setPositiveButton(R.string.dialog_open_alarm_settings) { _, _ ->
                        PermissionHelper.openExactAlarmSettings(activity)
                    }
                    .setNeutralButton(R.string.dialog_open_notification_settings) { _, _ ->
                        PermissionHelper.openNotificationSettings(activity)
                    }
                    .setNegativeButton(R.string.dialog_not_now, null)
                    .show()
            }
            needsExactAlarm -> showExactAlarmDialog(activity)
            needsNotifications -> showNotificationDialog(activity)
        }
    }
}
