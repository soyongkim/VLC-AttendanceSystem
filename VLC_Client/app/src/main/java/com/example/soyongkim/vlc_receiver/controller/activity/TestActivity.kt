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
import android.widget.ScrollView
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
class TestActivity : AppCompatActivity() {

    private lateinit var rcvdId : String
    private var rcvdType : Int = 0
    private lateinit var rcvdCookie : ByteArray
    private lateinit var rcvdAid : String
    private var rcvdState : Int = 0

    private val mExecutor = Executors.newCachedThreadPool()
    private var mSerialIoManager: SerialInputOutputManager? = null

    private lateinit var mDumpTextView : TextView
    private lateinit var mScrollView : ScrollView

    private val mListener = object : SerialInputOutputManager.Listener {
        override fun onRunError(e: Exception) {}

        override fun onNewData(data: ByteArray) {
            this@TestActivity.runOnUiThread {
                try {

                    rcvdId = TypeChangeUtil.byteToId(data)
                    rcvdType = TypeChangeUtil.byteToType(data)
                    rcvdCookie = TypeChangeUtil.byteToCookie(data)
                    rcvdAid = TypeChangeUtil.byteToAid(data)
                    rcvdState = TypeChangeUtil.byteToState(data)

                    updateReceivedData(data)

                } catch (e : Exception) {
                    Toast.makeText(this@TestActivity, "RcvError:$e",  Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateReceivedData(data: ByteArray) {
        var message = "Read : " + data.size + " bytes \n" + HexDump.toHexString(data) + "\n"
        // if you want to see formal data, use it
        message += "vtid($rcvdId) | type($rcvdType) | cookie(${HexDump.toHexString(rcvdCookie)}) | aid(${rcvdAid}) | state(${rcvdState})\n"
        mDumpTextView.append(message)
        mScrollView.smoothScrollTo(0, mDumpTextView.bottom)
    }

    /* Response callback Interface */
    interface IReceived {
        fun getResponseBody(msg: String)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vlc_test)

        mDumpTextView = findViewById(R.id.consoleText) as TextView
        mScrollView = findViewById(R.id.demoScroller) as ScrollView

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
}