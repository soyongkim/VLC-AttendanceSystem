package com.example.soyongkim.vlc_receiver.controller.util

import android.content.Context

interface HttpResponseEventRouter {
    fun route(context : Context, code : Int, arg : String)
}