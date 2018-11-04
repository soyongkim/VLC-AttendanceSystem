package com.example.soyongkim.vlc_receiver.controller.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

import com.example.soyongkim.vlc_receiver.R
import android.os.Handler
import android.widget.Toast
import com.example.soyongkim.vlc_receiver.model.item.UsbSingleton
import com.hoho.android.usbserial.driver.UsbSerialPort


class AttendanceStudentActivity : AppCompatActivity() {

    private lateinit var anim : Animation
    private lateinit var bAnim : AlphaAnimation

    private lateinit var sPort: UsbSerialPort

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode_student);

        findViewById<ImageView>(R.id.activity_icon).setImageResource(R.mipmap.logo_student)
        findViewById<TextView>(R.id.app_title).setText("Student")

        startMyAnimation(R.id.illust_phone)
        Handler().postDelayed(Runnable {
            startMyAnimation(R.id.illust_light)
            startMyAnimation(R.id.illust_char)
        }, 500)

        try {
            sPort = UsbSingleton.getUsbPort()
            Toast.makeText(this, "Check: " + sPort?.driver?.device?.deviceName, Toast.LENGTH_SHORT).show()
        } catch (e:Exception) {
            Toast.makeText(this, "Error:$e", Toast.LENGTH_LONG).show()
        }

    }

    private fun startMyAnimation(id: Int) {
        when(id) {
            R.id.illust_phone -> {
                anim = AnimationUtils.loadAnimation(applicationContext,android.R.anim.slide_in_left)
                findViewById<View>(id).startAnimation(anim)
            }
            R.id.illust_light -> {
                bAnim = AlphaAnimation(0.0f, 1.0f)
                bAnim.duration = 400
                bAnim.fillAfter = true
                bAnim.repeatMode = AlphaAnimation.REVERSE
                bAnim.repeatCount = 2
                findViewById<View>(id).startAnimation(bAnim)
                findViewById<View>(id).visibility = View.VISIBLE
            }
            R.id.illust_char -> {
                bAnim = AlphaAnimation(0.0f, 1.0f)
                bAnim.duration = 300
                bAnim.fillAfter = true
                bAnim.repeatMode = AlphaAnimation.REVERSE

                findViewById<View>(id).startAnimation(bAnim)
                findViewById<View>(id).visibility = View.VISIBLE
            }
        }
    }

}