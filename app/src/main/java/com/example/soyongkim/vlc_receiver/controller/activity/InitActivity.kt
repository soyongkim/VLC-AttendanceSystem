package com.example.soyongkim.vlc_receiver.controller.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import com.example.soyongkim.vlc_receiver.R

class InitActivity : AppCompatActivity() {

    private val TAG = InitActivity::class.java.simpleName
    private lateinit var btn_ipAddress:Button
    private lateinit var edit_ipAddress:EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init);

        btn_ipAddress = findViewById(R.id.btn_ipAddress)
        edit_ipAddress = findViewById(R.id.edit_ipAddress)

        btn_ipAddress.setOnClickListener() {
            //val intent = Intent(this, SelectModeActivity::class.java)
            val intent = Intent(this, DeviceListActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

}