package com.skybase.remindernotes.repository.room

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.skybase.humanizer.DateHumanizer
import com.skybase.remindernotes.R
import com.skybase.remindernotes.global.NoteApplication
import com.skybase.remindernotes.global.util.addField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

private const val DATABASE_NAME = "NoteDatabase"

class RoomDB {
    private var appDatabase: NoteDatabase? = null

    companion object {
        private var ourInstance: RoomDB? = null

        fun getDatabaseInstance(): NoteDatabase {
            if (ourInstance == null)
                ourInstance = RoomDB()

            return ourInstance!!.appDatabase as NoteDatabase
        }
    }

    init {
        val applicationInstant = NoteApplication.getApplicationInstance()

        appDatabase =
            Room.databaseBuilder(applicationInstant, NoteDatabase::class.java, DATABASE_NAME)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            getDatabaseInstance().noteDao().insertAllNote(populateNotes())
                        }
                    }
                })
                .build()
    }

    private fun populateNotes(): List<Note> {
        val contextResource = NoteApplication.getApplicationInstance().resources
        val time = Calendar.getInstance().addField(Calendar.MINUTE, 5).timeInMillis

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
                Body = contextResource.getString(
                    R.string.note_third_body,
                    DateHumanizer.humanize(
                        time,
                        DateHumanizer.TYPE_DD_MMM_YYYY,
                        DateHumanizer.TYPE_TIME_HH_MM_A
                    )
                ),
                Color = contextResource.getString(R.string.note_color_gray),
                Reminder = time
            )
        )
    }

}