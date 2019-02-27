package com.example.soyongkim.vlc_receiver.controller.activity

import android.content.Context
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
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
import com.example.soyongkim.vlc_receiver.controller.util.HttpResponseEventRouter
import com.example.soyongkim.vlc_receiver.controller.util.TypeChangeUtil
import com.example.soyongkim.vlc_receiver.model.item.UsbSingleton
import com.example.soyongkim.vlc_receiver.model.service.HttpRequestService
import com.example.soyongkim.vlc_receiver.view.ProgressDialog
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.util.HexDump
import com.hoho.android.usbserial.util.SerialInputOutputManager
import org.xmlpull.v1.XmlPullParser
import java.io.IOException
import java.io.StringReader
import java.sql.Time
import java.util.*
import java.util.concurrent.Executors

private var sPort: UsbSerialPort? = null
class AttendanceStudentActivity : AppCompatActivity() {

    companion object {
        const val MOVE = 7
        const val SUCCESS = 8
        const val FAIL = 9

        // VR state
        const val INIT_STATE = 5
        const val WAIT_SPEC_STATE = 6

        // VLC Frame Type
        const val IDLE = 0
        const val ACTIVE = 1
        const val VERIFY = 2
        const val RESULT = 3
    }

    private var vrState : Int = INIT_STATE

    private lateinit var anim : Animation
    private lateinit var bAnim : AlphaAnimation
    private lateinit var sid : String
    private var dialog: ProgressDialog? = null

    private lateinit var rcvdId : String
    private var rcvdType : Int = 0
    private lateinit var rcvdCookie : ByteArray
    private lateinit var rcvdAid : String

    private lateinit var bData : ByteArray
    internal var chkDupFrm = false

    private val mExecutor = Executors.newCachedThreadPool()
    private var mSerialIoManager: SerialInputOutputManager? = null

    private lateinit var receiveTimer: TimerTask

    private val mListener = object : SerialInputOutputManager.Listener {
        override fun onRunError(e: Exception) {}

        override fun onNewData(data: ByteArray) {
            this@AttendanceStudentActivity.runOnUiThread {
                try {
                    //bData = data

                    rcvdId = TypeChangeUtil.byteToId(data)
                    rcvdType = TypeChangeUtil.byteToType(data)
                    rcvdCookie = TypeChangeUtil.byteToCookie(data)
                    rcvdAid = TypeChangeUtil.byteToAid(data)

                    //Toast.makeText(this@AttendanceStudentActivity, "vtid($rcvdId) - vtidLength(${rcvdId.length})| type($rcvdType) | cookie(${HexDump.toHexString(rcvdCookie)}) | aid(${rcvdAid})", Toast.LENGTH_LONG).show()
                    //For Debugging the VLC data
                    //Toast.makeText(this@AttendanceStudentActivity, "recv_id:$rcvdId\nrecv_Type:$rcvdType\nData:${HexDump.dumpHexString(data)}\n", Toast.LENGTH_SHORT).show()

                    if(vrState == INIT_STATE) {
                        if(rcvdType == ACTIVE)
                            processVLCdata()
                    }
                    else if(vrState == WAIT_SPEC_STATE) {
                        if(rcvdType == VERIFY || rcvdType == RESULT) {
                            if(rcvdAid == sid && chkDupFrm == false) {
                                processVLCdata()
                            }
                        }
                    }
                } catch (e : Exception) {
                    Toast.makeText(this@AttendanceStudentActivity, "RcvError:$e",  Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun checkDupFrame(data : ByteArray) {
        if(bData != data) {
            chkDupFrm = false
        }
        else {
            Toast.makeText(this@AttendanceStudentActivity, "Dup Frame:$bData",  Toast.LENGTH_SHORT).show()
            chkDupFrm = true
        }

        bData = data
    }

    /* Response callback Interface */
    interface IReceived {
        fun getResponseBody(msg: String)
    }

    private fun processVLCdata() {
        when(rcvdType) {
            ACTIVE -> {
                Toast.makeText(applicationContext, "Received ACTIVE Frame", Toast.LENGTH_SHORT).show()
                var type = "ar"
                var arPayload = "<vtid>${rcvdId}</vtid><aid>${sid}</aid><type>${type}</type>"
                sendMessage(arPayload, type)
                vrState = WAIT_SPEC_STATE

            }
            VERIFY -> {
                Toast.makeText(applicationContext, "Received VERIFY Frame", Toast.LENGTH_SHORT).show()
                var type = "vr"
                var vrPayload = "<vtid>${rcvdId}</vtid><cookie>${HexDump.toHexString(rcvdCookie)}</cookie><aid>${sid}</aid><type>${type}</type>"
                sendMessage(vrPayload, type)
            }
            RESULT -> {
                Toast.makeText(applicationContext, "Received RESULT Frame", Toast.LENGTH_SHORT).show()
                var checkDup = HexDump.toHexString(rcvdCookie);
                if(checkDup == "11111111") {
                    Toast.makeText(applicationContext, "You already checked in this lecture", Toast.LENGTH_SHORT).show()
                    mHandler.sendMessage(mHandler.obtainMessage(AttendanceStudentActivity.FAIL))
                }
                else {
                    mHandler.sendMessage(mHandler.obtainMessage(AttendanceStudentActivity.SUCCESS))
                }
            }
        }
    }

    private var mHandler = Handler() {
        when (it.what) {
            AttendanceStudentActivity.MOVE -> {
                if(dialog != null) {
                    dialog!!.dismiss()
                    dialog = null
                    receiveTimer.cancel()
                    finish()
                }
            }
            AttendanceStudentActivity.SUCCESS -> handleSuccess()
            AttendanceStudentActivity.FAIL -> handleFail()
        }
        return@Handler true
    }

    private fun handleSuccess() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.changeAlertType(ProgressDialog.SUCCESS_TYPE)
            Toast.makeText(this@AttendanceStudentActivity, "Success to check Attendance", Toast.LENGTH_SHORT).show()
            mHandler.sendMessageDelayed(mHandler.obtainMessage(AttendanceStudentActivity.MOVE), 2000)
        }
//        Timer().schedule(object : TimerTask(){
//            override fun run() {
//                this@AttendanceStudentActivity.run {
//                    Toast.makeText(this@AttendanceStudentActivity, "It's not available key", Toast.LENGTH_SHORT).show()
//                }
//            }
//        } , 2000)
    }

    private fun handleFail() {
        if (dialog != null && dialog!!.isShowing) {
            mHandler.sendMessage(mHandler.obtainMessage(AttendanceStudentActivity.MOVE))
        }
    }

    private fun showDialog() {
        dialog = ProgressDialog(this@AttendanceStudentActivity)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    dialog!!.show()
                    startTimer()
                }
            }
        }, 200)
    }

    private fun startTimer() {
        receiveTimer = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    Toast.makeText(this@AttendanceStudentActivity, "Time out!", Toast.LENGTH_SHORT).show()
                    mHandler.sendMessage(mHandler.obtainMessage(AttendanceStudentActivity.FAIL))
                }
            }
        }
        Timer().schedule(receiveTimer, 10000)
    }

    private fun sendMessage(payload: String, type: String) {
        if(type == "ar") {
            showDialog()
        }
        HttpRequestService.getObject().httpRequestWithHandler(this@AttendanceStudentActivity, "POST",
                "/cnt-Client-Message", payload, 4,
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
                                //mHandler.sendMessageDelayed(mHandler.obtainMessage(AttendanceStudentActivity.SUCCESS), 1000)
                            } else {
                                //mHandler.sendMessageDelayed(mHandler.obtainMessage(AttendanceStudentActivity.FAIL), 1000)
                            }
                        }
                    }
                })
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

        try {
            sPort = UsbSingleton.getUsbPort()
        } catch (e:Exception) {
            Toast.makeText(this, "Error:$e", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        stopIoManager()
        if (sPort != null) {
            try {
                sPort!!.close()
            } catch (e: IOException) {
                // Ignore.
            }
            sPort = null
        }
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (sPort == null) {
            Toast.makeText(this, "Not have Device", Toast.LENGTH_SHORT).show()
        } else {
            var usbManager: UsbManager = getSystemService(Context.USB_SERVICE) as UsbManager
            var connection: UsbDeviceConnection? = usbManager.openDevice(sPort!!.driver.device)

            if (connection == null) {
                Toast.makeText(this, "Opening Device Fail", Toast.LENGTH_SHORT).show()
                return
            }

            try {
                sPort!!.open(connection)
                sPort!!.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
            } catch (e: IOException) {
                Toast.makeText(this, "Error opening device:$e", Toast.LENGTH_LONG).show()
                try {
                    sPort!!.close()
                } catch (e2: IOException) {
                    // Ignore.
                }
                sPort = null
                return
            }
        }
        onDeviceStateChange()
    }

    private fun onDeviceStateChange() {
        stopIoManager()
        startIoManager()
    }

    private fun stopIoManager() {
        if (mSerialIoManager != null) {
            mSerialIoManager!!.stop()
            mSerialIoManager = null
        }
    }

    private fun startIoManager() {
        if (com.example.soyongkim.vlc_receiver.controller.activity.sPort != null) {
            mSerialIoManager = SerialInputOutputManager(com.example.soyongkim.vlc_receiver.controller.activity.sPort, mListener)
            mExecutor.submit(mSerialIoManager)
        }
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