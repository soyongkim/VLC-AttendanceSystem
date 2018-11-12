package com.example.soyongkim.vlc_receiver.controller.activity

import com.example.soyongkim.vlc_receiver.R
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ProgressBar



/**
 * Created by Home on 2018-11-06.
 */
class TestActivity : AppCompatActivity() {

    internal lateinit var mprogressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.element_circular_progress_bar)

        mprogressBar = findViewById(R.id.circular_progress_bar)
    }
}