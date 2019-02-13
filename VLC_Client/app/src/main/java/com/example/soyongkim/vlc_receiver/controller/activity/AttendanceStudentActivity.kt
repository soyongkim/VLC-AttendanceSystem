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
import java.util.*
import java.util.concurrent.Executors

private var sPort: UsbSerialPort? = null
class AttendanceStudentActivity : AppCompatActivity() {

    companion object {
        const val MOVE = 5
        const val SUCCESS = 6
        const val ALREADY = 7
        const val FAIL = 8

        const val INIT_STATE = 0
        const val WAIT_SPEC_STATE = 1
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

    internal var rcvActivation = true

    var preMsg = ""

    private val mExecutor = Executors.newCachedThreadPool()
    private var mSerialIoManager: SerialInputOutputManager? = null

    private val mListener = object : SerialInputOutputManager.Listener {
        override fun onRunError(e: Exception) {}

        override fun onNewData(data: ByteArray) {
            this@AttendanceStudentActivity.runOnUiThread {
                try {

                    rcvdId = TypeChangeUtil.byteToId(data)
                    rcvdType = TypeChangeUtil.byteToType(data)
                    rcvdCookie = TypeChangeUtil.byteToCookie(data)
                    rcvdAid = TypeChangeUtil.byteToAid(data)

                    Toast.makeText(this@AttendanceStudentActivity, "vtid($rcvdId) | type($rcvdType) | cookie(${HexDump.toHexString(rcvdCookie)}) | aid(${rcvdAid})", Toast.LENGTH_LONG).show()
                    //For Debugging the VLC data
                    //Toast.makeText(this@AttendanceStudentActivity, "recv_id:$rcvdId\nrecv_Type:$rcvdType\nData:${HexDump.dumpHexString(data)}\n", Toast.LENGTH_SHORT).show()

                    if(vrState == INIT_STATE) {
                        if(rcvdType == 0)
                            processVLCdata()
                    }
                    else if(vrState == WAIT_SPEC_STATE) {
                        if(rcvdType == 1 || rcvdType == 2) {
                            if(rcvdAid == sid)
                                processVLCdata()
                        }
                    }
                } catch (e : Exception) {
                    Toast.makeText(this@AttendanceStudentActivity, "RcvError:$e",  Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateReceivedData(data: ByteArray) {
        val message = "Read : " + data.size + " bytes :\n" + HexDump.toHexString(data) + "\n"
        if(preMsg != message)
            Toast.makeText(applicationContext, "$message", Toast.LENGTH_LONG).show()
        preMsg = message
    }

    /* Response callback Interface */
    interface IReceived {
        fun getResponseBody(msg: String)
    }

    private fun processVLCdata() {
        when(rcvdType) {
            1 -> {
                Toast.makeText(applicationContext, "Received ACTIVE Frame", Toast.LENGTH_SHORT).show()
                // 1. AR 메시지보내기
                var arPayload = "<vtid>${rcvdId}</vtid><aid>${sid}</aid><type>ar</type>"
                sendMessage(arPayload)
                // 2. VERIFY 말고 모든 프레임 처리 막아두기. 중복 확인 체크를 위해 RESULT 프레임도 열어 둬야 겠다
                vrState = WAIT_SPEC_STATE
                // 3. 리시브 타이머 스타트

            }
            2 -> {
                Toast.makeText(applicationContext, "Received VERIFY Frame", Toast.LENGTH_SHORT).show()
                // 1. 내 VERIFY 인지 체크하기 -> 앞에서 했음
                var vrPayload = "<vtid>${rcvdId}</vtid><cookie>${HexDump.toHexString(rcvdCookie)}</cookie><aid>${sid}</aid><type>vr</type>"
                sendMessage(vrPayload)
                // 2. 맞으면 VR 메시지 보내기 -> 즉 그냥 보내기만 하면 됨
                // 3. RESULT 말고 모든 프레임 처리 막아두기 -> 이것도 하나의 스테이트가 되어 있어서 어차피 VERIFY 한번 보내고 나면 또 받아도 내껄로 받는게 아니니 앞에서 아이디 체크로 인해 걸러지기 때문에 이 작업은 필요없음
            }
            3 -> {
                var checkDup = HexDump.toHexString(rcvdCookie);
                if(checkDup == "11111111")
                    Toast.makeText(applicationContext, "You already checked in this lecture", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(applicationContext, "Attendance Checked", Toast.LENGTH_SHORT).show()
                // 2. 맞으면 확인과 함께 액티비티 종료
                finish()
            }
        }

        //sendMessage()
    }

    private var mHandler = Handler() {
        when (it.what) {
            AttendanceStudentActivity.MOVE -> {
                if(dialog != null) {
                    dialog!!.dismiss()
                    dialog = null
                    //rcvActivation = true
                }
            }
            AttendanceStudentActivity.SUCCESS -> handleSuccess()
            AttendanceStudentActivity.ALREADY -> handleFail("already")
            AttendanceStudentActivity.FAIL -> handleFail("fail")
        }

        return@Handler true
    }

    private fun handleSuccess() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.changeAlertType(ProgressDialog.SUCCESS_TYPE)
        }
        Timer().schedule(object : TimerTask(){
            override fun run() {
                this@AttendanceStudentActivity.run {
                    finish()
                }
            }
        } , 2000)
    }

    private fun handleFail(res: String) {
        if(res == "fail")
            Toast.makeText(this@AttendanceStudentActivity, "It's not available key", Toast.LENGTH_SHORT).show()
        else if(res == "already") {
            Toast.makeText(this@AttendanceStudentActivity, "You are already checked", Toast.LENGTH_SHORT).show()
            finish()
        }
        if (dialog != null && dialog!!.isShowing) {
            mHandler.sendMessage(mHandler.obtainMessage(AttendanceStudentActivity.MOVE))
        }
    }

    private fun sendMessage(payload: String) {
        dialog = ProgressDialog(this@AttendanceStudentActivity)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
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
                                Log.d("Student_code", "code:${code} arg:${arg} con:${con}")
                                if(con == "fail") {
                                    //mHandler.sendMessageDelayed(mHandler.obtainMessage(AttendanceStudentActivity.FAIL), 1000)
                                } else if(con == "already_success"){
                                    //mHandler.sendMessageDelayed(mHandler.obtainMessage(AttendanceStudentActivity.ALREADY), 1000)
                                } else {
                                    //mHandler.sendMessageDelayed(mHandler.obtainMessage(AttendanceStudentActivity.SUCCESS), 1000)
                                }
                            } else {
                                //mHandler.sendMessageDelayed(mHandler.obtainMessage(AttendanceStudentActivity.FAIL), 1000)
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