package com.example.soyongkim.vlc_receiver.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ProgressBar
import com.example.soyongkim.vlc_receiver.R


/**
 * Created by Home on 2018-11-13.
 */
class ProgressDialog(context : Context) : Dialog(context) {

    private lateinit var progressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_circular_progress_bar)
        progressBar = findViewById(R.id.circular_progress_bar)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

    }
}
