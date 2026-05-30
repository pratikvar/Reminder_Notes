package com.skybase.remindernotes.view

import android.view.View
import androidx.core.content.ContextCompat
import com.skybase.remindernotes.R
import com.skybase.remindernotes.databinding.ActivityNewNoteBinding
import com.skybase.remindernotes.databinding.ListitemNoteBinding
import com.skybase.remindernotes.viewmodel.NoteModel

object NoteUiBinder {

    fun bindListItem(binding: ListitemNoteBinding, note: NoteModel) {
        binding.tvTitle.text = note.title
        binding.tvBody.text = note.body
        binding.layoutNoteContent.setBackgroundColor(note.displayColor)
        binding.layoutOverlapIcons.setBackgroundColor(note.displayColor)

        binding.ivPinned.visibility = if (note.isPinned) View.VISIBLE else View.GONE

        if (note.isReminderAdded) {
            binding.tvReminderInfo.visibility = View.VISIBLE
            binding.tvReminderInfo.text = note.displayReminder
        } else {
            binding.tvReminderInfo.visibility = View.GONE
        }
    }

    fun bindNewNote(
        binding: ActivityNewNoteBinding,
        note: NoteModel,
        updateTextFields: Boolean
    ) {
        if (updateTextFields) {
            binding.etNoteTitle.setText(note.title)
            binding.etNoteBody.setText(note.body)
        }

        binding.layoutScroll.setBackgroundColor(note.displayColor)

        if (note.isReminderAdded) {
            binding.tvReminderInfo.visibility = View.VISIBLE
            binding.tvReminderInfo.text = binding.root.context.getString(
                R.string.note_reminder_info,
                note.displayReminder
            )
            val colorRes = if (note.isReminderDateValid) {
                R.color.note_valid_color
            } else {
                R.color.note_invalid_color
            }
            binding.tvReminderInfo.setTextColor(
                ContextCompat.getColor(binding.root.context, colorRes)
            )
            binding.ivReminderWarning.visibility =
                if (note.isReminderDateValid) View.GONE else View.VISIBLE
        } else {
            binding.tvReminderInfo.visibility = View.GONE
            binding.ivReminderWarning.visibility = View.GONE
        }
    }
}
