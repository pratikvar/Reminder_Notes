package com.skybase.remindernotes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.skybase.remindernotes.repository.NoteRepository

class NoteActivityViewModel : ViewModel() {
    var mNotes: LiveData<List<NoteModel>> = NoteRepository.getAllNotesLive()
}