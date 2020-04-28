package com.skybase.remindernotes.global.util

import com.skybase.remindernotes.R
import com.skybase.remindernotes.global.NoteApplication

object ColorUtil {
    fun getColorInt(color: String): Int {
        return when (color) {
            NoteApplication.getApplicationInstance().resources.getString(R.string.note_color_gray)
            -> NoteApplication.getApplicationInstance().resources.getColor(R.color.note_gray_200)
            NoteApplication.getApplicationInstance().resources.getString(R.string.note_color_green)
            -> NoteApplication.getApplicationInstance().resources.getColor(R.color.note_green_200)
            NoteApplication.getApplicationInstance().resources.getString(R.string.note_color_blue)
            -> NoteApplication.getApplicationInstance().resources.getColor(R.color.note_blue_200)
            NoteApplication.getApplicationInstance().resources.getString(R.string.note_color_purple)
            -> NoteApplication.getApplicationInstance().resources.getColor(R.color.note_purple_200)
            NoteApplication.getApplicationInstance().resources.getString(R.string.note_color_red)
            -> NoteApplication.getApplicationInstance().resources.getColor(R.color.note_red_200)
            NoteApplication.getApplicationInstance().resources.getString(R.string.note_color_teal)
            -> NoteApplication.getApplicationInstance().resources.getColor(R.color.note_teal_200)
            NoteApplication.getApplicationInstance().resources.getString(R.string.note_color_yellow)
            -> NoteApplication.getApplicationInstance().resources.getColor(R.color.note_yellow_200)
            else -> NoteApplication.getApplicationInstance().resources.getColor(R.color.note_gray_200)
        }
    }
}