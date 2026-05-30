package com.skybase.remindernotes.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.skybase.remindernotes.R
import com.skybase.remindernotes.databinding.ActivityNoteBinding
import com.skybase.remindernotes.global.AlarmScheduler
import com.skybase.remindernotes.global.util.PermissionDialogs
import com.skybase.remindernotes.global.util.PermissionHelper
import com.skybase.remindernotes.global.util.SystemBarHelper
import com.skybase.remindernotes.repository.NoteRepository
import com.skybase.remindernotes.repository.room.Note
import com.skybase.remindernotes.view.adapter.NoteActivityAdapter
import com.skybase.remindernotes.viewmodel.NoteActivityViewModel
import com.skybase.remindernotes.viewmodel.NoteModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class NoteActivity : AppCompatActivity(), NoteActivityAdapter.OnNoteInteractionListener {

    private lateinit var binding: ActivityNoteBinding
    private lateinit var adapter: NoteActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SystemBarHelper.enableEdgeToEdgeWithLightBackground(this)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        PermissionHelper.registerNotificationPermission(this) {
            updatePermissionBanner()
        }
        binding.btnPermissionSettings.setOnClickListener {
            showBannerSettingsDialog()
        }
        setupRecyclerView()
        registerViewModel()
        registerListeners()
    }

    override fun onResume() {
        super.onResume()
        updatePermissionBanner()
    }

    private fun showBannerSettingsDialog() {
        PermissionDialogs.showSettingsChooserForHints(
            activity = this,
            needsExactAlarm = !PermissionHelper.canScheduleExactAlarms(this),
            needsNotifications = !PermissionHelper.areNotificationsEnabled(this)
        )
    }

    private fun updatePermissionBanner() {
        val messages = mutableListOf<String>()
        if (!PermissionHelper.areNotificationsEnabled(this)) {
            messages.add(getString(R.string.permission_banner_notifications))
        }
        if (!PermissionHelper.canScheduleExactAlarms(this)) {
            messages.add(getString(R.string.permission_banner_alarms))
        }

        if (messages.isEmpty()) {
            binding.cardPermissionBanner.visibility = View.GONE
        } else {
            binding.cardPermissionBanner.visibility = View.VISIBLE
            binding.tvPermissionMessage.text = messages.joinToString("\n\n")
        }
    }

    private fun registerListeners() {
        binding.fabAddNote.setOnClickListener {
            openNoteCreationActivity()
        }
    }

    private fun openNoteCreationActivity(noteId: Int = 0) {
        val intent = Intent(this@NoteActivity, NewNoteActivity::class.java)
        if (noteId != 0) {
            intent.putExtra(resources.getString(R.string.intent_extra_id), noteId)
        }
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        binding.rvNotes.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        adapter = NoteActivityAdapter(this)
        binding.rvNotes.adapter = adapter
    }

    private fun registerViewModel() {
        val viewModel = ViewModelProvider(this)[NoteActivityViewModel::class.java]
        viewModel.mNotes.observe(this, this::updateDataSet)
    }

    private fun updateDataSet(updatedList: List<NoteModel>?) {
        adapter.updateDataSet(updatedList)
    }

    override fun onNoteClicked(noteModel: NoteModel?) {
        openNoteCreationActivity(noteId = noteModel?.Id?.toInt() ?: 0)
    }

    override fun onNotePinClicked(noteModel: NoteModel?) {
        CoroutineScope(Dispatchers.IO).launch {
            updateNoteWithPin(noteModel)
        }
    }

    private fun updateNoteWithPin(noteModel: NoteModel?) {
        if (noteModel != null) {
            noteModel.isPinned = !noteModel.isPinned
            noteModel.pinnedOn =
                if (noteModel.isPinned) Calendar.getInstance().timeInMillis else null
            val note = Note(
                Id = noteModel.Id,
                Title = noteModel.title,
                Body = noteModel.body,
                Color = noteModel.color,
                Reminder = noteModel.reminder,
                IsPinned = noteModel.isPinned,
                PinnedOn = noteModel.pinnedOn
            )
            NoteRepository.insertNote(note = note)
        }
    }

    override fun onNoteShareClicked(noteModel: NoteModel?) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, noteModel?.title + "\n" + noteModel?.body)
        }
        startActivity(Intent.createChooser(shareIntent, "Share Note Via"))
    }

    override fun onNoteDeleteClicked(noteModel: NoteModel?) {
        CoroutineScope(Dispatchers.IO).launch {
            val noteId = noteModel!!.Id!!.toInt()
            val note = NoteRepository.getNoteForId(noteId)
            AlarmScheduler.cancelReminder(noteId)
            NoteRepository.deleteNote(noteId = noteId)

            withContext(Dispatchers.Main) {
                Snackbar.make(binding.root, "Note Deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        undoNoteDelete(note!!)
                    }.show()
            }
        }
    }

    private fun undoNoteDelete(note: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            NoteRepository.insertNote(note)
        }
    }
}
