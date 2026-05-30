package com.skybase.remindernotes.viewmodel

import com.skybase.remindernotes.global.util.ColorUtil
import com.skybase.remindernotes.global.util.DateFormatUtil
import com.skybase.remindernotes.global.util.setTimeMillis
import java.util.Calendar

data class NoteModel(
    var Id: Int? = null,
    var title: String = "",
    var body: String = "",
    var color: String = "",
    var reminder: Long? = null,
    var isPinned: Boolean = false,
    var pinnedOn: Long? = null
) {
    val isReminderAdded: Boolean
        get() = reminder != null

    val displayReminder: String
        get() = DateFormatUtil.formatReminder(reminder)

    val displayColor: Int
        get() = ColorUtil.getColorInt(color)

    val isReminderDateValid: Boolean
        get() = Calendar.getInstance().before(
            Calendar.getInstance().setTimeMillis(reminder ?: Calendar.getInstance().timeInMillis)
        )
}
