package com.example.soyongkim.vlc_receiver.controller.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.soyongkim.vlc_receiver.controller.fragment.TabMainFragment
import com.example.soyongkim.vlc_receiver.controller.util.*


class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotificationUtil.showTimerExpired(context)

        //when alarm manager is counting and finished,
        Toast.makeText(context, "Time out by Alarm manager", Toast.LENGTH_SHORT).show()

        PrefUtil.setTimerState(TabMainFragment.TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)
    }
}
