package com.example.soyongkim.vlc_receiver.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.example.soyongkim.vlc_receiver.R
import com.example.soyongkim.vlc_receiver.controller.activity.AttendanceStudentActivity
import com.example.soyongkim.vlc_receiver.controller.activity.InitActivity
import com.example.soyongkim.vlc_receiver.controller.fragment.TabListFragment
import com.example.soyongkim.vlc_receiver.controller.util.HttpResponseEventRouter
import com.example.soyongkim.vlc_receiver.model.service.HttpRequestService
import kotlinx.android.synthetic.main.activity_mode_admin.*
import java.util.*

/**
 * Created by Home on 2018-11-15.
 */
class LoginDialog(context : Context?) : Dialog(context) {
    private lateinit var editId : EditText
    private lateinit var editPwd : EditText
    private lateinit var btnLogin : Button

    private var dialog : ProgressDialog? = null

    private var mHandler = Handler() {
        when (it.what) {
            InitActivity.MOVE -> {
                if(dialog != null) {
                    dialog!!.dismiss()
                    dialog = null
                    this.dismiss()
                }
            }
            InitActivity.GET_SUCCESS -> handleSuccess()
            InitActivity.GET_FAIL -> handleFail()
        }

        return@Handler true
    }

    private fun handleSuccess() {
        if (dialog != null && dialog!!.isShowing) {
            val intent = Intent(context, AttendanceStudentActivity::class.java)
            //Toast.makeText(context, "edit:${editId.text}", Toast.LENGTH_SHORT).show()
            intent.putExtra("sid", editId.text.toString())
            intent.putExtra("tmp", editPwd.text.toString())
            context.startActivity(intent)
        }

        mHandler.sendMessage(mHandler.obtainMessage(InitActivity.MOVE))

    }

    private fun handleFail() {
        Toast.makeText(context, "The ID or password error", Toast.LENGTH_SHORT).show()
        if (dialog != null && dialog!!.isShowing) {
            mHandler.sendMessage(mHandler.obtainMessage(InitActivity.MOVE))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_login)

        this.editId = findViewById(R.id.etId)
        this.editPwd = findViewById(R.id.etPassword)
        this.btnLogin = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener(View.OnClickListener {
            var id = editId.text
            var password = editPwd.text
            dialog = ProgressDialog(context)
            HttpRequestService.getObject().httpRequestWithHandler(context, "GET",
                    "/std_" + id,
                    object : HttpResponseEventRouter {
                        override fun route(context: Context, code: Int, arg: String) {
                            Log.d("code", "code:${code} arg:${arg}")
                            if(code == 200) {
                                mHandler.sendMessageDelayed(mHandler.obtainMessage(InitActivity.GET_SUCCESS), 1000)
                            }
                            else {
                                mHandler.sendMessageDelayed(mHandler.obtainMessage(InitActivity.GET_FAIL), 1000)
                            }
                        }
                    })
            dialog!!.show()
        })
    }
}