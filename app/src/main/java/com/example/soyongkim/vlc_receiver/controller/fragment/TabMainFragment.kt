package com.example.soyongkim.vlc_receiver.controller.fragment

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import com.example.soyongkim.vlc_receiver.R
import android.os.Bundle
import android.os.CountDownTimer
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.Toast
import com.example.soyongkim.vlc_receiver.controller.receiver.TimerExpiredReceiver
import com.example.soyongkim.vlc_receiver.controller.util.*
import kotlinx.android.synthetic.main.element_timer.*
import kotlinx.android.synthetic.main.fragment_admin_main.*
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import java.io.Serializable
import java.util.*
import android.support.v4.view.accessibility.AccessibilityEventCompat.setAction
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import com.example.soyongkim.vlc_receiver.controller.activity.AttendanceAdminActivity
import kotlinx.android.synthetic.main.activity_custom_snack_bar.*


class TabMainFragment : Fragment() {

    companion object {
        fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long{
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefUtil.setAlarmSetTime(nowSeconds, context)
            return wakeUpTime
        }

        fun removeAlarm(context: Context){
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0, context)
        }

        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000
    }

    enum class TimerState : Serializable {
        Stopped, Paused, Running
    }

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds: Long = 0
    private var timerState = TimerState.Stopped
    private var secondsRemaining: Long = 0

    private lateinit var btn_start: FloatingActionButton
    private lateinit var btn_stop: FloatingActionButton
    private lateinit var btn_setting: FloatingActionButton


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view : View = inflater.inflate(R.layout.fragment_admin_main, container, false)

        this.btn_start = view.findViewById(R.id.fab_start)
        this.btn_stop = view.findViewById(R.id.fab_stop)
        this.btn_setting = view.findViewById(R.id.fab_setting)

        btn_start.setOnClickListener {v->
            startTimer()
            timerState = TimerState.Running
            updateButtons()
        }
        btn_stop.setOnClickListener { v ->
            timer.cancel()
            onTimerFinished()
        }
        btn_setting.setOnClickListener { v ->
           startSnackbar(v)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        initTimer()
        //when resuming, the activity is counting, not alarm manager
        removeAlarm(context!!)
        NotificationUtil.hideTimerNotification(context)
    }

    override fun onPause() {
        super.onPause()
        saveTimer()
    }

    private fun saveTimer()
    {
        if (timerState == TimerState.Running){
            timer.cancel()
            //but when pausing, alarm manager is counting, not activity
            val wakeUpTime = setAlarm(context!!, nowSeconds, secondsRemaining)
            NotificationUtil.showTimerRunning(context!!, wakeUpTime)
        }
        else if (timerState == TimerState.Paused){
            NotificationUtil.showTimerPaused(context!!)
        }

        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, context)
        PrefUtil.setSecondsRemaining(secondsRemaining, context)
        PrefUtil.setTimerState(timerState, context)
    }

    private fun initTimer() {
        timerState = PrefUtil.getTimerState(context)
        if(timerState == TimerState.Stopped)
            setNewTimerLength()
        else
            setPreviousTimerLength()


        secondsRemaining = if (timerState == TimerState.Running)
            PrefUtil.getSecondsRemaining(context)
        else
            timerLengthSeconds

        val alarmSetTime = PrefUtil.getAlarmSetTime(context)
        if(alarmSetTime > 0) {
            secondsRemaining -= nowSeconds - alarmSetTime
        }
        if(secondsRemaining <= 0)
            onTimerFinished()
        else if(timerState == TimerState.Running) {
            startTimer()
        }

        updateButtons()
        updateCountdownUI()
    }

    private fun setNewTimerLength(){
        val lengthInMinutes = PrefUtil.getTimerLength(context)
        timerLengthSeconds = (lengthInMinutes * 60L)
        progress_countdown.max = timerLengthSeconds.toInt()
    }

    private fun setPreviousTimerLength(){
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(context)
        progress_countdown.max = timerLengthSeconds.toInt()
    }

    private fun onTimerFinished(){
        timerState = TimerState.Stopped
        PrefUtil.setTimerState(timerState, context)
        //set the length of the timer to be the one set in SettingsActivity
        //if the length was changed when the timer was running
        setNewTimerLength()

        progress_countdown.progress = 0

        PrefUtil.setSecondsRemaining(timerLengthSeconds, context)
        secondsRemaining = timerLengthSeconds

        updateButtons()
        updateCountdownUI()
    }

    private fun startTimer(){
        timerState = TimerState.Running

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() {
                //if the activity is counting and finished
                Toast.makeText(context, "Timeout by Activity", Toast.LENGTH_SHORT).show()
                onTimerFinished()
            }

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }

    private fun updateButtons(){
        when (timerState) {
            TimerState.Running ->{
                fab_start.isEnabled = false
                fab_setting.isEnabled = false
                fab_stop.isEnabled = true
            }
            TimerState.Stopped -> {
                fab_start.isEnabled = true
                fab_setting.isEnabled = true
                fab_stop.isEnabled = false
            }
            TimerState.Paused -> {
                fab_start.isEnabled = false
                fab_setting.isEnabled = false
                fab_stop.isEnabled = false
            }
        }
    }

    private fun updateCountdownUI(){
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        textView_countdown.text = "$minutesUntilFinished:${if (secondsStr.length == 2) secondsStr else "0" + secondsStr}"
        progress_countdown.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }

    private fun startSnackbar(v: View) {
        // Disable swiping viewpager during set the time length
        (activity as AttendanceAdminActivity).setSwipeable(false)
        // To disable the buttons
        TimerState.Paused
        updateButtons()

        val snackbar: Snackbar = Snackbar.make(v, "", Snackbar.LENGTH_INDEFINITE)
        val snackbarView = snackbar.view as Snackbar.SnackbarLayout

        snackbarView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener{
            override fun onViewAttachedToWindow(v: View?) {
            }
            override fun onViewDetachedFromWindow(v: View?) {
                // if the snackbar is dismissed,
                updateButtons()
                (activity as AttendanceAdminActivity).setSwipeable(true)
                initTimer()
            }
        })

        val customView = layoutInflater.inflate(R.layout.activity_custom_snack_bar, null)
        var setTime: Int = 10
        var txt : TextView = customView.findViewById(R.id.txt_snackbar)
        var sb : SeekBar = customView.findViewById(R.id.seekbar_snackbar)
        sb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }
            override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                           fromUser: Boolean) {
                //if time length is changed,
                txt.setText("> " + progress + "min")
                setTime = progress
            }
        })

        var btn : Button = customView.findViewById(R.id.btn_snackbar)
        btn.setOnClickListener{
            PrefUtil.setTimerLength(setTime, context)
            snackbar.dismiss()
        }

        snackbarView.addView(customView, 0)
        snackbar.show()
    }

}
