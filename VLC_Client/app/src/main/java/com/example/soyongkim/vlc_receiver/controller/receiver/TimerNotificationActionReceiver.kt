package com.example.soyongkim.vlc_receiver.controller.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.soyongkim.vlc_receiver.controller.fragment.TabMainFragment
import com.example.soyongkim.vlc_receiver.controller.util.NotificationUtil
import com.example.soyongkim.vlc_receiver.controller.util.PrefUtil
import com.example.soyongkim.vlc_receiver.model.action.AppConstants

class TimerNotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AppConstants.ACTION_STOP -> {
                TabMainFragment.removeAlarm(context)
                PrefUtil.setTimerState(TabMainFragment.TimerState.Stopped, context)
                NotificationUtil.hideTimerNotification(context)
            }
            AppConstants.ACTION_PAUSE -> {
                var secondsRemaining = PrefUtil.getSecondsRemaining(context)
                val alarmSetTime = PrefUtil.getAlarmSetTime(context)
                val nowSeconds = TabMainFragment.nowSeconds

                secondsRemaining -= nowSeconds - alarmSetTime
                PrefUtil.setSecondsRemaining(secondsRemaining, context)

                TabMainFragment.removeAlarm(context)
                PrefUtil.setTimerState(TabMainFragment.TimerState.Paused, context)
                NotificationUtil.showTimerPaused(context)
            }
            AppConstants.ACTION_RESUME -> {
                val secondsRemaining = PrefUtil.getSecondsRemaining(context)
                val wakeUpTime = TabMainFragment.setAlarm(context, TabMainFragment.nowSeconds, secondsRemaining)
                PrefUtil.setTimerState(TabMainFragment.TimerState.Running, context)
                NotificationUtil.showTimerRunning(context, wakeUpTime)
            }
            AppConstants.ACTION_START -> {
                val minutesRemaining = PrefUtil.getTimerLength(context)
                val secondsRemaining = minutesRemaining * 60L
                val wakeUpTime = TabMainFragment.setAlarm(context, TabMainFragment.nowSeconds, secondsRemaining)
                PrefUtil.setTimerState(TabMainFragment.TimerState.Running, context)
                PrefUtil.setSecondsRemaining(secondsRemaining, context)
                NotificationUtil.showTimerRunning(context, wakeUpTime)
            }
        }
    }
}
