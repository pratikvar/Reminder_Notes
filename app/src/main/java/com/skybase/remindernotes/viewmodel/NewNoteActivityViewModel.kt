package com.skybase.remindernotes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.skybase.remindernotes.repository.NoteRepository

class NewNoteActivityViewModel : ViewModel() {
    private val mNoteId = MutableLiveData(0)

    val noteModel: LiveData<NoteModel> = mNoteId.switchMap { id ->
        NoteRepository.getNoteModelForIdLive(id)
    }

    fun setNoteId(noteId: Int) {
        mNoteId.value = noteId
    }
}
