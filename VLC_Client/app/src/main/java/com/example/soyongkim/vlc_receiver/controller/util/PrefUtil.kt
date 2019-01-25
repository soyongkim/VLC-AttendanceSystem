package com.example.soyongkim.vlc_receiver.controller.util

import android.content.Context
import android.preference.PreferenceManager
import com.example.soyongkim.vlc_receiver.controller.fragment.TabMainFragment


class PrefUtil {
    companion object {

        private const val TIMER_LENGTH_ID = "com.example.soyongkim.timer_length"

        fun getTimerLength(context: Context?): Int{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(TIMER_LENGTH_ID, 10)
        }

        fun setTimerLength(seconds: Int, context: Context?){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putInt(TIMER_LENGTH_ID, seconds)
            editor.apply()
        }


        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "com.example.soyongkim.previous_timer_length_seconds"

        fun getPreviousTimerLengthSeconds(context: Context?): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0)
        }

        fun setPreviousTimerLengthSeconds(seconds: Long, context: Context?){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }


        private const val TIMER_STATE_ID = "com.example.soyongkim.timer_state"

        fun getTimerState(context: Context?): TabMainFragment.TimerState{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
            return TabMainFragment.TimerState.values()[ordinal]
        }

        fun setTimerState(state: TabMainFragment.TimerState, context: Context?){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }


        private const val SECONDS_REMAINING_ID = "com.example.soyongkim.seconds_remaining"

        fun getSecondsRemaining(context: Context?): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS_REMAINING_ID, 0)
        }

        fun setSecondsRemaining(seconds: Long, context: Context?){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID, seconds)
            editor.apply()
        }


        private const val ALARM_SET_TIME_ID = "com.example.soyongkim.backgrounded_time"

        fun getAlarmSetTime(context: Context?): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return  preferences.getLong(ALARM_SET_TIME_ID, 0)
        }

        fun setAlarmSetTime(time: Long, context: Context?){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID, time)
            editor.apply()
        }

        private const val RESTART_STATE_ID = "com.example.soyongkim.restart_state"

        fun getRestartState(context: Context?): Int{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return  preferences.getInt(RESTART_STATE_ID, 0)
        }

        fun setRestartState(state: Int, context: Context?){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putInt(RESTART_STATE_ID, state)
            editor.apply()
        }
    }
}