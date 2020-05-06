package com.skybase.remindernotes.repository.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skybase.remindernotes.viewmodel.NoteModel

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllNote(note: List<Note>)

    @Query("SELECT * FROM Note WHERE Id=:noteId")
    fun getNoteModelForIdLive(noteId: Int): LiveData<NoteModel>

    @Query("SELECT * FROM Note WHERE Id=:noteId")
    fun getNoteModelForId(noteId: Int): NoteModel?

    @Query("SELECT * FROM Note WHERE Id=:noteId")
    fun getNoteForId(noteId: Int): Note?

    @Query(
        "SELECT * FROM Note " +
                "ORDER BY isPinned DESC, DATETIME(PinnedOn) DESC "
    )
    fun getAllNotesLive(): LiveData<List<NoteModel>>

    @Query("SELECT * FROM Note")
    fun getAllNotes(): List<NoteModel>

    @Query("DELETE FROM Note WHERE Id=:noteId")
    fun deleteNote(noteId: Int)
}