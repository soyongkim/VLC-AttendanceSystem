package com.example.soyongkim.vlc_receiver.controller.activity

import android.app.Activity
import android.content.Context
import com.example.soyongkim.vlc_receiver.R
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.app.AppCompatActivity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.example.soyongkim.vlc_receiver.model.item.UsbSingleton
import com.hoho.android.usbserial.driver.UsbSerialPort

private lateinit var mDector: GestureDetectorCompat
private lateinit var sPort: UsbSerialPort

class SelectModeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_mode)

        mDector = GestureDetectorCompat(this, MyGestureListener(this, findViewById(R.id.card_view)))
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mDector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    fun onClick(view: View) {
        when(view.id) {
            R.id.btn_ModeAttendance ->{
                val intent = Intent(this, AttendanceActivity::class.java)
                this.startActivity(intent)
            }
            R.id.btn_ModeMuseum->{
                val intent = Intent(this, MuseumActivity::class.java)
                this.startActivity(intent)
            }
        }
    }

    private class MyGestureListener constructor(private var activity: Activity, private var v: View)
        : GestureDetector.SimpleOnGestureListener()
            , Animation.AnimationListener {

        override fun onDown(event: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
                event1: MotionEvent,
                event2: MotionEvent,
                velocityX: Float,
                velocityY: Float
        ): Boolean {

            if ((event2.eventTime - event1.eventTime) < 200) {
                if ((event2.y - event1.y) > 300) {
                    if (v.visibility == View.GONE) {
                        v.visibility = View.VISIBLE
                        activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
                        val animation = AnimationUtils.loadAnimation(activity, R.anim.slide_down)
                        v.startAnimation(animation)
                    }
                } else if ((event2.y - event1.y) < -300) {
                    if (v.visibility == View.VISIBLE) {
                        val animation = AnimationUtils.loadAnimation(activity, R.anim.slide_up)
                        animation.setAnimationListener(this)
                        animation.fillAfter = true
                        v.startAnimation(animation)
                    }
                }
            }
            return true
        }

        override fun onAnimationEnd(animation: Animation?) {
            this.v.visibility = View.GONE
        }

        override fun onAnimationRepeat(animation: Animation?) {
        }

        override fun onAnimationStart(animation: Animation?) {
        }
    }

    companion object {
        fun show(context: Context, port: UsbSerialPort?) {
            UsbSingleton.setUsbPort(port!!)
            sPort = UsbSingleton.getUsbPort()
            val intent = Intent(context, SelectModeActivity::class.java)
            context.startActivity(intent)
        }
    }
}



