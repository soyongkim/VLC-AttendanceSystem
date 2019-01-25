package com.example.soyongkim.vlc_receiver.controller.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.soyongkim.vlc_receiver.controller.fragment.TabMainFragment
import com.example.soyongkim.vlc_receiver.controller.util.*
import com.example.soyongkim.vlc_receiver.model.service.HttpRequestService


class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        //NotificationUtil.showTimerExpired(context)
        //when alarm manager is counting and finished,
        Toast.makeText(context, "Stop", Toast.LENGTH_SHORT).show()
        PrefUtil.setTimerState(TabMainFragment.TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)
        NotificationUtil.hideTimerNotification(context)
        requestStop(context)
    }

    private fun requestStop(context: Context) {
        var query : String = "stop"
        var resource = "/cnt-ps-key"
        HttpRequestService.getObject().httpRequestWithHandler(context!!, "POST",
                resource, query , 4,
                object : HttpResponseEventRouter {
                    override fun route(context: Context, code: Int, arg: String) {
                        if(code !== 201) Toast.makeText(context, "Fail to send Stop message. Start again", Toast.LENGTH_SHORT).show()
                    }
                })
    }
}
