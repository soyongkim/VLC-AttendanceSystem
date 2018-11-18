package com.example.soyongkim.vlc_receiver.controller.activity

import android.app.Activity
import android.content.Context
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
import android.util.Log
import android.util.Xml
import android.widget.Toast
import com.example.soyongkim.vlc_receiver.controller.fragment.TabListFragment
import com.example.soyongkim.vlc_receiver.controller.util.HttpResponseEventRouter
import com.example.soyongkim.vlc_receiver.model.item.UsbSingleton
import com.example.soyongkim.vlc_receiver.model.service.HttpRequestService
import com.example.soyongkim.vlc_receiver.view.ProgressDialog
import com.hoho.android.usbserial.driver.UsbSerialPort
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import java.util.*


class AttendanceStudentActivity : AppCompatActivity() {

    private lateinit var anim : Animation
    private lateinit var bAnim : AlphaAnimation
    private lateinit var sPort: UsbSerialPort
    private lateinit var sid : String
    private var dialog: ProgressDialog? = null

    private var mHandler = Handler() {
        when (it.what) {
            TabListFragment.MOVE -> {
                if(dialog != null) {
                    dialog!!.dismiss()
                    dialog = null
                }
            }
            TabListFragment.GET -> handleSuccess("GET")
            TabListFragment.POST -> handleSuccess("POST")
            TabListFragment.FAIL -> handleFail()
        }

        return@Handler true
    }

    private fun handleSuccess(req : String) {
        if (dialog != null && dialog!!.isShowing) {
            finish()
        }
        mHandler.sendMessage(mHandler.obtainMessage(TabListFragment.MOVE))

    }

    private fun handleFail() {
        Toast.makeText(this@AttendanceStudentActivity, "Fail to load", Toast.LENGTH_SHORT).show()
        if (dialog != null && dialog!!.isShowing) {
            mHandler.sendMessage(mHandler.obtainMessage(TabListFragment.MOVE))
        }
    }

    private fun sendMessage() {
        dialog = ProgressDialog(this@AttendanceStudentActivity)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        HttpRequestService.getObject().httpRequestWithHandler(this@AttendanceStudentActivity, "POST",
                "/cnt-ps-key", "<sid>${sid}</sid><key>${intent.getStringExtra("tmp")}</key>", 4,
                object : HttpResponseEventRouter {
                    override fun route(context: Context, code: Int, arg: String) {
                        runOnUiThread {
                            if (code == 201) {
                                var parser : XmlPullParser = Xml.newPullParser()
                                parser.setInput(StringReader(arg))
                                var eventType = parser.eventType
                                var flag = 0
                                var con : String = ""
                                while (eventType != XmlPullParser.END_DOCUMENT) {
                                    if (eventType == XmlPullParser.START_DOCUMENT) {
                                        // XML 데이터 시작
                                    } else if (eventType == XmlPullParser.START_TAG) {
                                        // 시작 태그가 파싱. "<TAG>"
                                        if(parser.getName() == "con")
                                            flag = 1
                                    } else if (eventType == XmlPullParser.TEXT) {
                                        // 시작 태그와 종료 태그 사이의 텍스트. "<TAG>TEXT</TAG>"
                                        if(flag == 1) {
                                            con = parser.getText()
                                            flag = 0
                                        }
                                    }
                                    eventType = parser.next()
                                }
                                Log.d("Student_code", "code:${code} arg:${arg} con:${con}")
                                if(con == "fail") {
                                    mHandler.sendMessageDelayed(mHandler.obtainMessage(TabListFragment.FAIL), 1000)
                                } else {
                                    mHandler.sendMessageDelayed(mHandler.obtainMessage(TabListFragment.GET), 1000)
                                }
                            } else {
                                //Toast.makeText(context, arg, Toast.LENGTH_SHORT).show()
                                mHandler.sendMessageDelayed(mHandler.obtainMessage(TabListFragment.FAIL), 1000)
                            }
                        }

                    }
                })

        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    dialog!!.show()
                }
            }
        }, 200)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode_student)

        sid = intent.getStringExtra("sid")

        findViewById<ImageView>(R.id.activity_icon).setImageResource(R.mipmap.logo_student)
        findViewById<TextView>(R.id.app_title).text = "Student"

        startMyAnimation(R.id.illust_phone)
        Handler().postDelayed(Runnable {
            startMyAnimation(R.id.illust_light)
            startMyAnimation(R.id.illust_char)
        }, 500)

//        try {
//            sPort = UsbSingleton.getUsbPort()
//            Toast.makeText(this, "Check: " + sPort?.driver?.device?.deviceName, Toast.LENGTH_SHORT).show()
//        } catch (e:Exception) {
//            Toast.makeText(this, "Error:$e", Toast.LENGTH_LONG).show()
//        }

        sendMessage()
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