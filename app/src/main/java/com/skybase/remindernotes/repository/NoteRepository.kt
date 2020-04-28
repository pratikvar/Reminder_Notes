package com.skybase.remindernotes.repository

import androidx.lifecycle.LiveData
import com.skybase.remindernotes.repository.room.Note
import com.skybase.remindernotes.repository.room.NoteDao
import com.skybase.remindernotes.repository.room.RoomDB
import com.skybase.remindernotes.viewmodel.NoteModel

object NoteRepository {

    fun getNoteForId(noteId: Int): NoteModel? {
        return getNoteDao().getNoteForId(noteId)
    }

    fun getNoteForIdLive(noteId: Int): LiveData<NoteModel> {
        return getNoteDao().getNoteForIdLive(noteId)
    }

    fun getAllNotesLive(): LiveData<List<NoteModel>> {
        return getNoteDao().getAllNotesLive()
    }

    fun getAllNotes(): List<NoteModel> {
        return getNoteDao().getAllNotes()
    }

    fun insertNote(note: Note): Long {
        return getNoteDao().insertNote(note)
    }

    fun deleteNote(noteId: Int) {
        getNoteDao().deleteNote(noteId)
    }

    private fun getNoteDao(): NoteDao {
        return RoomDB.getDatabaseInstance().noteDao()
    }

}