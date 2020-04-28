package com.skybase.remindernotes.global

import android.app.Application

class NoteApplication : Application() {

    companion object {
        var INSTANCE: NoteApplication? = null

        fun getApplicationInstance(): NoteApplication {
            return INSTANCE as NoteApplication
        }
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}