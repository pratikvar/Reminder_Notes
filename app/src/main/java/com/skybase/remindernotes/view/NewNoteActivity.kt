package com.skybase.remindernotes.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.skybase.remindernotes.R
import com.skybase.remindernotes.databinding.ActivityNewNoteBinding
import com.skybase.remindernotes.global.util.DateTimeUtil
import com.skybase.remindernotes.global.util.setFiled
import com.skybase.remindernotes.repository.NoteRepository
import com.skybase.remindernotes.repository.room.Note
import com.skybase.remindernotes.viewmodel.NewNoteActivityViewModel
import com.skybase.remindernotes.viewmodel.NoteModel
import kotlinx.android.synthetic.main.activity_new_note.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class NewNoteActivity : AppCompatActivity() {

    lateinit var mBinding: ActivityNewNoteBinding
    lateinit var mViewModel: NewNoteActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = Slide(Gravity.TOP)
            exitTransition = Slide(Gravity.BOTTOM)
        }

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_note)

        getIntentData()
        registerListeners()
    }

    //<editor-fold desc="init">
    private fun getIntentData() {
        val noteId = intent.getIntExtra(resources.getString(R.string.intent_extra_id), 0)
        registerViewModel(noteId)
    }

    private fun registerViewModel(noteId: Int) {
        mViewModel = ViewModelProviders.of(this).get(NewNoteActivityViewModel::class.java)
        mViewModel.setNoteId(noteId)
        mViewModel.noteModel.observe(this,
            Observer { mBinding.note = it ?: NoteModel() })
    }

    private fun registerListeners() {
        btn_save.setOnClickListener {
            validateAndSaveNote()
        }

        setupColorListeners()
        setupDialogListeners()
    }

    //<editor-fold desc="listeners">

    private fun setupColorListeners() {
        val onColorClickListener: View.OnClickListener = View.OnClickListener {
            mBinding.note!!.color = when (it) {
                btn_color_blue -> resources.getString(R.string.note_color_blue)
                btn_color_gray -> resources.getString(R.string.note_color_gray)
                btn_color_green -> resources.getString(R.string.note_color_green)
                btn_color_purple -> resources.getString(R.string.note_color_purple)
                btn_color_red -> resources.getString(R.string.note_color_red)
                btn_color_teal -> resources.getString(R.string.note_color_teal)
                btn_color_yellow -> resources.getString(R.string.note_color_yellow)
                else -> resources.getString(R.string.note_color_gray)
            }
        }

        for (child in layout_scroll_color.children) {
            if (child is ImageView)
                child.setOnClickListener(onColorClickListener)
        }
    }

    private fun setupDialogListeners() {
        val cal = Calendar.getInstance()

        btn_reminder.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this, { _, year, month, dayOfMonth ->
                    dateSelected(year, month, dayOfMonth)
                }
                , cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.show()
        }
    }

    private fun dateSelected(year: Int, month: Int, dayOfMonth: Int) {
        val cal = Calendar.getInstance()
            .setFiled(Calendar.YEAR, year)
            .setFiled(Calendar.MONTH, month)
            .setFiled(Calendar.DAY_OF_MONTH, dayOfMonth)

        mBinding.note!!.reminder = cal.timeInMillis

        val timePickerDialog = TimePickerDialog(
            this, { _, hourOfDay, minute ->
                timeSelected(hourOfDay, minute)
            }, 12, 0, false
        )

        timePickerDialog.show()
    }

    private fun timeSelected(hourOfDay: Int, minute: Int) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = mBinding.note!!.reminder ?: Calendar.getInstance().timeInMillis

        cal.setFiled(Calendar.HOUR_OF_DAY, hourOfDay)
            .setFiled(Calendar.MINUTE, minute)

        mBinding.note!!.reminder = cal.timeInMillis
    }
    //</editor-fold>

    //<editor-fold desc="save note">
    private fun validateAndSaveNote() {
        if (mBinding.note!!.body.isBlank()) {
            Toast.makeText(this@NewNoteActivity, R.string.validation_new_note, Toast.LENGTH_SHORT)
                .show()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                saveNote()
            }
        }
    }

    private suspend fun saveNote() {
        val pair = getNoteFromNoteModel()
        val noteId = NoteRepository.insertNote(note = pair.first)

        if (pair.first.Reminder != null && pair.second)
            DateTimeUtil.setAlarmForReminder(pair.first.Reminder!!, noteId = noteId.toInt())

        withContext(Dispatchers.Main) {
            Toast.makeText(this@NewNoteActivity, "Note Saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun getNoteFromNoteModel(): Pair<Note, Boolean> {
        val noteModel = mBinding.note
        val note: Note = Note(noteModel!!.Id, noteModel.title, noteModel.body, noteModel.color)
        note.Reminder = noteModel.reminder
        if (note.Title.isBlank()) {
            note.Title =
                note.Body.substring(0, if (note.Body.length < 24) note.Body.length else 24)
        }
        return Pair(note, noteModel.isReminderDateValid)
    }
    //</editor-fold>
    //</editor-fold>
}
