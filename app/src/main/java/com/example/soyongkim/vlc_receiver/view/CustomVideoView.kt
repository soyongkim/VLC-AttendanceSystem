package com.example.soyongkim.vlc_receiver.view

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.VideoView


class CustomVideoView : VideoView {
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    private fun init(context: Context) {}


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        val displayMetrics = DisplayMetrics()
        (getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager).getDefaultDisplay().getMetrics(displayMetrics)
        val deviceWidth = displayMetrics.widthPixels
        val deviceHeight = displayMetrics.heightPixels
        setMeasuredDimension(deviceWidth, deviceHeight)
    }
}