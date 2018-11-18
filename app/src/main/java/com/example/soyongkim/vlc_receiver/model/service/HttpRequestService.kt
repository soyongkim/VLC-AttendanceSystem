package com.example.soyongkim.vlc_receiver.model.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.soyongkim.vlc_receiver.controller.util.HttpResponseEventRouter
import com.example.soyongkim.vlc_receiver.model.onem2m.CSEBase
import com.example.soyongkim.vlc_receiver.model.onem2m.ContentInstanceObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.spec.ECField

@Suppress("SENSELESS_COMPARISON")
class HttpRequestService : Service(){
    private var cnt = 0

    companion object {
        private lateinit var thisPointer : HttpRequestService
        fun getObject() : HttpRequestService{
            return HttpRequestService.thisPointer
        }

        lateinit var cseBase : CSEBase
    }

    fun httpRequestWithHandler(context : Context, method : String, urlResource : String, router : HttpResponseEventRouter?)
    {
        object : Thread(){
            override fun run() {
                var urlString : String = cseBase.serviceUrl + urlResource
                val url = URL(urlString)
                val urlConn = url.openConnection() as HttpURLConnection
                urlConn.setRequestProperty("content-type" , "application/xml")
                urlConn.setRequestProperty("X-M2M-RI", "12345")
                urlConn.setRequestProperty("X-M2M-Origin" , "VLC-Receiver")
                urlConn.setRequestProperty("nmtype" , "long")
                urlConn.requestMethod = method

                try {
                    val reader = BufferedReader(InputStreamReader(urlConn.inputStream))

                    val line = StringBuilder()
                    while (true) {
                        val l = reader.readLine() ?: break
                        line.append(l)
                    }
                    router?.route(context , urlConn.responseCode , line.toString())

                } catch(e:Exception) {
                    router?.route(context , 400 , "E:$e")
                }
            }
        }.start()
    }

    fun httpRequestWithHandler(context : Context , method : String, urlResource : String, payload : String , type : Int, router : HttpResponseEventRouter?)
    {
        object : Thread(){
            override fun run() {
                val urlString : String = cseBase.serviceUrl + urlResource
                val url = URL(urlString)
                val urlConn = url.openConnection() as HttpURLConnection
                var reqContent : String = ""

                if(type == 4) {
                    reqContent = ContentInstanceObject(payload).makeXML()
                    urlConn.setRequestProperty("Accept", "application/xml")
                    urlConn.setRequestProperty("Content-Type", "application/vnd.onem2m-res+xml;ty=4")
                    urlConn.setRequestProperty("locale", "ko")
                    urlConn.setRequestProperty("X-M2M-RI", "12345")
                    urlConn.setRequestProperty("X-M2M-Origin", "VLC-Receiver")
                    urlConn.setRequestProperty("Content-Length", reqContent.length.toString())
                }
                urlConn.requestMethod = method

                try {
                    val writer = PrintStream(urlConn.outputStream)
                    writer.print(reqContent)

                    val reader = BufferedReader(InputStreamReader(urlConn.inputStream))

                    val line = StringBuilder()

                    while (true) {
                        val l = reader.readLine() ?: break
                        line.append(l)
                    }

                    router?.route(context , urlConn.responseCode , line.toString())
                } catch(e:Exception) {
                    router?.route(context , 400 , "E:$e")
                }
            }
        }.start()
    }

    fun disconnect(){
        this.cnt--;

        if(this.cnt == 0){
            val intent = Intent(this , HttpRequestService::class.java)
            stopService(intent)
        }

    }

    override fun onCreate() {
        HttpRequestService.thisPointer = this
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        this.cnt++;
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}