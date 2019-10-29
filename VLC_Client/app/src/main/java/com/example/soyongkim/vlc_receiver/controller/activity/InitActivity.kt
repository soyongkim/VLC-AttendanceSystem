package com.example.soyongkim.vlc_receiver.controller.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import cn.pedant.sweetalert.SweetAlertDialog
import com.example.soyongkim.vlc_receiver.R
import com.example.soyongkim.vlc_receiver.controller.util.HttpResponseEventRouter
import com.example.soyongkim.vlc_receiver.model.onem2m.CSEBase
import com.example.soyongkim.vlc_receiver.model.service.HttpRequestService
import com.example.soyongkim.vlc_receiver.controller.util.PermissonAllow
import java.util.*

class InitActivity : AppCompatActivity() {

    companion object {
        const val MOVE = 0
        const val GET_SUCCESS = 1
        const val GET_FAIL = 2
    }

    private val TAG = InitActivity::class.java.simpleName
    private lateinit var btn_ipAddress:Button
    private lateinit var edit_ipAddress:EditText
    private var dialog: SweetAlertDialog? = null

    private var mHandler = Handler() {
        when (it.what) {
            InitActivity.MOVE -> {
                if(dialog != null) {
                    dialog!!.dismiss()
                    dialog = null
                }
                Timer().schedule(object : TimerTask(){
                    override fun run() {
                        this@InitActivity.run {
                            val intent = Intent(this, DeviceListActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        }
                    }
                } , 200)

            }

            InitActivity.GET_SUCCESS -> handleGetSuccess()
            InitActivity.GET_FAIL -> handleGetFail()
        }

        return@Handler true
    }

    private fun handleGetSuccess() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
            mHandler.sendMessageDelayed(mHandler.obtainMessage(InitActivity.MOVE), 2000)
        }else {
            mHandler.sendMessage(mHandler.obtainMessage(InitActivity.MOVE))
        }
    }

    private fun handleGetFail() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.changeAlertType(SweetAlertDialog.ERROR_TYPE)
            dialog!!.showCancelButton(true)
        }
    }

    fun onClick(view: View) {
        HttpRequestService.cseBase = CSEBase(edit_ipAddress.text.toString(), "7579", "Mobius")
        dialog = SweetAlertDialog(this@InitActivity, SweetAlertDialog.FINGER_TYPE)
        dialog!!.titleText = "Search"
        dialog!!.contentText = "Searching for VLC-Manager..."
        HttpRequestService.getObject().httpRequestWithHandler(this, "GET",
                "",
                object : HttpResponseEventRouter {
                    override fun route(context: Context, code: Int, arg: String) {
                        this@InitActivity.runOnUiThread {
                            if(code == 200)
                                mHandler.sendMessageDelayed(mHandler.obtainMessage(InitActivity.GET_SUCCESS), 1000)

                            else
                                mHandler.sendMessageDelayed(mHandler.obtainMessage(InitActivity.GET_FAIL), 1000)
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
        setContentView(R.layout.activity_init)

        btn_ipAddress = findViewById(R.id.btn_ipAddress)
        edit_ipAddress = findViewById(R.id.edit_ipAddress)

        PermissonAllow.setupPermissions(this)

        var intent = Intent(this, HttpRequestService::class.java)
        startService(intent)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        HttpRequestService.getObject().disconnect()
    }

}