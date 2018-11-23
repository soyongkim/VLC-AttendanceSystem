package com.example.soyongkim.vlc_receiver.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.widget.FrameLayout
import android.widget.ProgressBar
import cn.pedant.sweetalert.OptAnimationLoader
import cn.pedant.sweetalert.SuccessTickView
import com.example.soyongkim.vlc_receiver.R


/**
 * Created by Home on 2018-11-13.
 */
class ProgressDialog(context : Context) : Dialog(context) {

    companion object {
        const val NORMAL_TYPE = 0
        const val ERROR_TYPE = 1
        const val SUCCESS_TYPE = 2
        const val WARNING_TYPE = 3
        const val CUSTOM_IMAGE_TYPE = 4
        const val FINGER_TYPE = 5
    }

    private var mDialogView : View? = null
    private var mAlertType: Int = 0
    private var mSuccessFrame: FrameLayout? = null
    private lateinit var mSuccessLayoutAnimSet: AnimationSet
    private lateinit var mSuccessBowAnim: Animation
    private var mProgressFrame: FrameLayout? = null


    private var mSuccessTick: SuccessTickView? = null
    private var mSuccessLeftMask: View? = null
    private var mSuccessRightMask: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_circular_progress_bar)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        mDialogView = window!!.decorView.findViewById(android.R.id.content)
        mSuccessBowAnim = OptAnimationLoader.loadAnimation(context, cn.pedant.sweetalert.R.anim.success_bow_roate)
        mSuccessLayoutAnimSet = OptAnimationLoader.loadAnimation(context, cn.pedant.sweetalert.R.anim.success_mask_layout) as AnimationSet
        mSuccessFrame = findViewById<FrameLayout>(R.id.success_frame)
        mSuccessTick = mSuccessFrame!!.findViewById(R.id.success_tick) as SuccessTickView
        mSuccessLeftMask = mSuccessFrame!!.findViewById(R.id.mask_left)
        mSuccessRightMask = mSuccessFrame!!.findViewById(R.id.mask_right)
        mProgressFrame = findViewById<FrameLayout>(R.id.circular_progress_bar)

    }

    private fun playAnimation() {
        if (mAlertType == ERROR_TYPE) {
            //mErrorFrame.startAnimation(mErrorInAnim)
            //mErrorX.startAnimation(mErrorXInAnim)
        } else if (mAlertType == SUCCESS_TYPE) {
            mSuccessTick!!.startTickAnim()
            mSuccessRightMask!!.startAnimation(mSuccessBowAnim)
        }
    }

    private fun changeAlertType(alertType: Int, fromCreate: Boolean) {
        mAlertType = alertType
        // call after created views
        if (mDialogView != null) {
            if (!fromCreate) {
                // restore all of views state before switching alert type
                restore()
            }
            when (mAlertType) {
                //ERROR_TYPE -> mErrorFrame.setVisibility(View.VISIBLE)
                SUCCESS_TYPE -> {
                    mProgressFrame!!.visibility = View.GONE
                    mSuccessFrame!!.visibility = View.VISIBLE
                    // initial rotate layout of success mask
                    mSuccessLeftMask!!.startAnimation(mSuccessLayoutAnimSet.animations[0])
                    mSuccessRightMask!!.startAnimation(mSuccessLayoutAnimSet.animations[1])
                }
            }
            if (!fromCreate) {
                playAnimation()
            }
        }
    }

    fun changeAlertType(alertType: Int) {
        changeAlertType(alertType, false)
    }

    private fun restore() {
        mSuccessFrame!!.visibility = View.GONE
        mSuccessTick!!.clearAnimation()
        mSuccessLeftMask!!.clearAnimation()
        mSuccessRightMask!!.clearAnimation()
    }
}
