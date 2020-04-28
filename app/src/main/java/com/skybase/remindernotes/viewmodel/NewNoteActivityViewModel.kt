package com.skybase.remindernotes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.skybase.remindernotes.repository.NoteRepository

class NewNoteActivityViewModel : ViewModel() {
    private var mNoteId: MutableLiveData<Int> = MutableLiveData<Int>()
    var noteModel: LiveData<NoteModel> =
        Transformations.switchMap(mNoteId) { input -> NoteRepository.getNoteForIdLive(input) }

    fun setNoteId(noteId: Int) {
        mNoteId.value = noteId
    }
}