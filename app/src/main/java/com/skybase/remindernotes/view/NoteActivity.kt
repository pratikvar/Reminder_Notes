package com.skybase.remindernotes.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.skybase.remindernotes.R
import com.skybase.remindernotes.databinding.ActivityNoteBinding
import com.skybase.remindernotes.repository.NoteRepository
import com.skybase.remindernotes.repository.room.Note
import com.skybase.remindernotes.view.adapter.NoteActivityAdapter
import com.skybase.remindernotes.viewmodel.NoteActivityViewModel
import com.skybase.remindernotes.viewmodel.NoteModel
import kotlinx.android.synthetic.main.activity_note.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class NoteActivity : AppCompatActivity(), NoteActivityAdapter.OnNoteInteractionListener {

    private val STATE_ALL_NOTES: Int = 0
    private val STATE_REMINDER_NOTES: Int = 1
    private val STATE_NORMAL_NOTES: Int = 2

    lateinit var mBinding: ActivityNoteBinding
    lateinit var mAdapter: NoteActivityAdapter
    private var mCurrentState = STATE_ALL_NOTES

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_note)
        setupRecyclerView()
        registerViewModel()
        registerListeners()
    }

    private fun registerListeners() {
        fab_add_note.setOnClickListener {
            openNoteCreationActivity()
        }
    }

    private fun openNoteCreationActivity(noteId: Int = 0) {
        val intent = Intent(this@NoteActivity, NewNoteActivity::class.java)
        if (noteId != 0)
            intent.putExtra(resources.getString(R.string.intent_extra_id), noteId)
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        rv_notes.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mAdapter = NoteActivityAdapter(this)
        rv_notes.adapter = mAdapter
    }

    private fun registerViewModel() {
        val viewModel: NoteActivityViewModel by lazy {
            ViewModelProviders.of(this).get(NoteActivityViewModel::class.java)
        }
        viewModel.mNotes.observe(
            this,
            Observer(this::updateDataSet)
        )
    }

    private fun updateDataSet(updatedList: List<NoteModel>?) {
        mAdapter.updateDataSet(updatedList)
    }

    //<editor-fold desc="override">
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
            val note: Note = Note(
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
        val shareIntent: Intent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, noteModel?.title + "\n" + noteModel?.body)
        startActivity(Intent.createChooser(shareIntent, "Share Note Via"))
    }

    override fun onNoteDeleteClicked(noteModel: NoteModel?) {
        CoroutineScope(Dispatchers.IO).launch {
            NoteRepository.deleteNote(noteId = noteModel!!.Id!!.toInt())
        }
    }

    //</editor-fold>
}
