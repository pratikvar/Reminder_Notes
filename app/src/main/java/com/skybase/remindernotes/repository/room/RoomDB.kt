package com.skybase.remindernotes.repository.room

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.skybase.remindernotes.R
import com.skybase.remindernotes.global.NoteApplication
import com.skybase.remindernotes.global.util.DateFormatUtil
import com.skybase.remindernotes.global.util.addField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

private const val DATABASE_NAME = "NoteDatabase"

class RoomDB private constructor() {

    private val appDatabase: NoteDatabase

    init {
        val applicationInstant = NoteApplication.getApplicationInstance()
        appDatabase = Room.databaseBuilder(applicationInstant, NoteDatabase::class.java, DATABASE_NAME)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        appDatabase.noteDao().insertAllNote(populateNotes())
                    }
                }
            })
            .build()
    }

    private fun populateNotes(): List<Note> {
        val contextResource = NoteApplication.getApplicationInstance().resources
        val time = Calendar.getInstance().addField(Calendar.MINUTE, 15).timeInMillis
        val formattedTime = DateFormatUtil.formatReminder(time)

        return listOf(
            Note(
                Id = null,
                Title = contextResource.getString(R.string.note_one_title),
                Body = contextResource.getString(R.string.note_one_body),
                Color = contextResource.getString(R.string.note_color_gray)
            ),
            Note(
                Id = null,
                Title = contextResource.getString(R.string.note_second_title),
                Body = contextResource.getString(R.string.note_second_body),
                Color = contextResource.getString(R.string.note_color_green)
            ),
            Note(
                Id = null,
                Title = contextResource.getString(R.string.note_third_title),
                Body = contextResource.getString(R.string.note_third_body, formattedTime),
                Color = contextResource.getString(R.string.note_color_gray),
                Reminder = time
            )
        )
    }

    companion object {
        @Volatile
        private var instance: RoomDB? = null

        fun getDatabaseInstance(): NoteDatabase {
            return instance?.appDatabase ?: synchronized(this) {
                val roomDb = instance ?: RoomDB().also { instance = it }
                roomDb.appDatabase
            }
        }
    }
}
