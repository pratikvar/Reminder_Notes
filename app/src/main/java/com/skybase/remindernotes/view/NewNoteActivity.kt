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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import com.skybase.remindernotes.R
import com.skybase.remindernotes.databinding.ActivityNewNoteBinding
import com.skybase.remindernotes.global.util.DateTimeUtil
import com.skybase.remindernotes.global.util.PermissionDialogs
import com.skybase.remindernotes.global.util.PermissionHelper
import com.skybase.remindernotes.global.util.SystemBarHelper
import com.skybase.remindernotes.global.util.setFiled
import com.skybase.remindernotes.repository.NoteRepository
import com.skybase.remindernotes.repository.room.Note
import com.skybase.remindernotes.viewmodel.NewNoteActivityViewModel
import com.skybase.remindernotes.viewmodel.NoteModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class NewNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewNoteBinding
    private lateinit var viewModel: NewNoteActivityViewModel
    private var currentNote = NoteModel(color = "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SystemBarHelper.enableEdgeToEdgeWithLightBackground(this)
        with(window) {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition = Slide(Gravity.TOP)
            exitTransition = Slide(Gravity.BOTTOM)
        }

        binding = ActivityNewNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        if (currentNote.color.isEmpty()) {
            currentNote = currentNote.copy(color = getString(R.string.note_color_gray))
        }

        getIntentData()
        registerListeners()
    }

    private fun getIntentData() {
        val noteId = intent.getIntExtra(resources.getString(R.string.intent_extra_id), 0)
        registerViewModel(noteId)
    }

    private fun registerViewModel(noteId: Int) {
        viewModel = ViewModelProvider(this)[NewNoteActivityViewModel::class.java]
        viewModel.setNoteId(noteId)
        viewModel.noteModel.observe(this) { model ->
            currentNote = model ?: NoteModel(color = getString(R.string.note_color_gray))
            refreshUi(updateTextFields = true)
        }
    }

    override fun onResume() {
        super.onResume()
        updateReminderPermissionHints()
    }

    private fun refreshUi(updateTextFields: Boolean) {
        NoteUiBinder.bindNewNote(binding, currentNote, updateTextFields)
        updateReminderPermissionHints()
    }

    private fun updateReminderPermissionHints() {
        if (!currentNote.isReminderAdded) {
            binding.tvReminderPermissionHint.visibility = View.GONE
            return
        }

        val hints = mutableListOf<String>()
        if (!PermissionHelper.canScheduleExactAlarms(this)) {
            hints.add(getString(R.string.permission_reminder_alarm_hint))
        }
        if (!PermissionHelper.areNotificationsEnabled(this)) {
            hints.add(getString(R.string.permission_reminder_notification_hint))
        }

        if (hints.isEmpty()) {
            binding.tvReminderPermissionHint.visibility = View.GONE
        } else {
            binding.tvReminderPermissionHint.visibility = View.VISIBLE
            binding.tvReminderPermissionHint.text = hints.joinToString(" ")
            binding.tvReminderPermissionHint.setOnClickListener {
                PermissionDialogs.showSettingsChooserForHints(
                    activity = this,
                    needsExactAlarm = !PermissionHelper.canScheduleExactAlarms(this),
                    needsNotifications = !PermissionHelper.areNotificationsEnabled(this)
                )
            }
        }
    }

    private fun registerListeners() {
        binding.btnSave.setOnClickListener {
            validateAndSaveNote()
        }
        setupColorListeners()
        setupDialogListeners()
    }

    private fun setupColorListeners() {
        val onColorClickListener = View.OnClickListener {
            currentNote = currentNote.copy(
                color = when (it.id) {
                    R.id.btn_color_blue -> getString(R.string.note_color_blue)
                    R.id.btn_color_gray -> getString(R.string.note_color_gray)
                    R.id.btn_color_green -> getString(R.string.note_color_green)
                    R.id.btn_color_purple -> getString(R.string.note_color_purple)
                    R.id.btn_color_red -> getString(R.string.note_color_red)
                    R.id.btn_color_teal -> getString(R.string.note_color_teal)
                    R.id.btn_color_yellow -> getString(R.string.note_color_yellow)
                    else -> getString(R.string.note_color_gray)
                }
            )
            refreshUi(updateTextFields = false)
        }

        for (child in binding.layoutScrollColor.children) {
            if (child is ImageView) {
                child.setOnClickListener(onColorClickListener)
            }
        }
    }

    private fun setupDialogListeners() {
        val cal = Calendar.getInstance()

        binding.btnReminder.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    dateSelected(year, month, dayOfMonth)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun dateSelected(year: Int, month: Int, dayOfMonth: Int) {
        val cal = Calendar.getInstance()
            .setFiled(Calendar.YEAR, year)
            .setFiled(Calendar.MONTH, month)
            .setFiled(Calendar.DAY_OF_MONTH, dayOfMonth)

        currentNote = currentNote.copy(reminder = cal.timeInMillis)
        refreshUi(updateTextFields = false)

        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                timeSelected(hourOfDay, minute)
            },
            12,
            0,
            false
        ).show()
    }

    private fun timeSelected(hourOfDay: Int, minute: Int) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = currentNote.reminder ?: Calendar.getInstance().timeInMillis
        cal.setFiled(Calendar.HOUR_OF_DAY, hourOfDay)
            .setFiled(Calendar.MINUTE, minute)
        currentNote = currentNote.copy(reminder = cal.timeInMillis)
        refreshUi(updateTextFields = false)
    }

    private fun validateAndSaveNote() {
        syncTextFieldsToNote()
        if (currentNote.body.isBlank()) {
            Toast.makeText(this@NewNoteActivity, R.string.validation_new_note, Toast.LENGTH_SHORT)
                .show()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                saveNote()
            }
        }
    }

    private fun syncTextFieldsToNote() {
        currentNote = currentNote.copy(
            title = binding.etNoteTitle.text.toString(),
            body = binding.etNoteBody.text.toString()
        )
    }

    private suspend fun saveNote() {
        val pair = getNoteFromNoteModel()
        val hasValidReminder = pair.first.Reminder != null && pair.second
        val noteId = NoteRepository.insertNote(note = pair.first).toInt()

        var alarmScheduled = true
        if (hasValidReminder) {
            alarmScheduled = DateTimeUtil.setAlarmForReminder(pair.first.Reminder!!, noteId)
        }

        withContext(Dispatchers.Main) {
            Toast.makeText(this@NewNoteActivity, R.string.note_saved, Toast.LENGTH_SHORT).show()

            val needsExactAlarm = hasValidReminder && !alarmScheduled
            val needsNotifications =
                hasValidReminder && !PermissionHelper.areNotificationsEnabled(this@NewNoteActivity)

            if (needsExactAlarm || needsNotifications) {
                PermissionDialogs.showReminderSetupDialog(
                    activity = this@NewNoteActivity,
                    needsExactAlarm = needsExactAlarm,
                    needsNotifications = needsNotifications,
                    onComplete = { finish() }
                )
            } else {
                finish()
            }
        }
    }

    private fun getNoteFromNoteModel(): Pair<Note, Boolean> {
        val note = Note(currentNote.Id, currentNote.title, currentNote.body, currentNote.color)
        note.Reminder = currentNote.reminder
        if (note.Title.isBlank()) {
            note.Title = note.Body.substring(0, minOf(note.Body.length, 24))
        }
        return Pair(note, currentNote.isReminderDateValid)
    }
}
