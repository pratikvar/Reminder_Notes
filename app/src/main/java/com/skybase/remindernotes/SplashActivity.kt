package com.skybase.remindernotes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.skybase.remindernotes.view.NoteActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var intent = Intent(this, NoteActivity::class.java)
        startActivity(intent)
    }
}
