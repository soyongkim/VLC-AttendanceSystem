package com.example.soyongkim.vlc_receiver.controller.activity

import android.content.Context
import com.example.soyongkim.vlc_receiver.R
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.soyongkim.vlc_receiver.controller.fragment.TabMainFragment
import com.example.soyongkim.vlc_receiver.controller.util.NotificationUtil
import com.example.soyongkim.vlc_receiver.controller.util.PrefUtil
import com.example.soyongkim.vlc_receiver.model.item.UsbSingleton
import com.hoho.android.usbserial.driver.UsbSerialPort

private lateinit var sPort: UsbSerialPort


class SelectModeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_mode)
    }

    override fun onResume() {
        super.onResume()
        // TImer stop on TimerNotification
        TabMainFragment.removeAlarm(this)
        PrefUtil.setTimerState(TabMainFragment.TimerState.Stopped, this)
        NotificationUtil.hideTimerNotification(this)
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


    companion object {
        fun show(context: Context, port: UsbSerialPort?) {
            UsbSingleton.setUsbPort(port!!)
            sPort = UsbSingleton.getUsbPort()
            val intent = Intent(context, SelectModeActivity::class.java)
            context.startActivity(intent)
        }
    }
}



