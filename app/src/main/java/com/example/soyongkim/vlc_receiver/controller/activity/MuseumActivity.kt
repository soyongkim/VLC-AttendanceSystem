package com.example.soyongkim.vlc_receiver.controller.activity

import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import com.example.soyongkim.vlc_receiver.controller.util.*

import com.example.soyongkim.vlc_receiver.R
import com.example.soyongkim.vlc_receiver.model.item.Content
import com.example.soyongkim.vlc_receiver.model.item.UsbSingleton
import com.example.soyongkim.vlc_receiver.model.service.HttpRequestService
import com.example.soyongkim.vlc_receiver.view.CustomVideoView
import com.example.soyongkim.vlc_receiver.view.ProgressDialog
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.util.*
import kotlinx.android.synthetic.main.activity_mode_museum.*
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors

private var sPort: UsbSerialPort? = null

class MuseumActivity : AppCompatActivity() {
    companion object {
        const val MOVE = 0
        const val SUCCESS = 1
        const val FAIL = 2
    }
    private lateinit var recyclerView: RecyclerView
    private var mAdapter: ContentListAdapter = ContentListAdapter(this)
    private var layoutManager = LinearLayoutManager(this)
    internal lateinit var intent: Intent

    private lateinit var contentDesc: ImageView
    private lateinit var contentVideo: CustomVideoView
    internal var mPlayer: MediaPlayer? = null

    private lateinit var anim : Animation
    private lateinit var bAnim : AlphaAnimation

    internal var rcvdId = 1
    internal var rcvdType = 0
    internal var pastId = 3

    private val mExecutor = Executors.newCachedThreadPool()
    private var mSerialIoManager: SerialInputOutputManager? = null

    lateinit var videoTimer: Timer
    var task: TimerTask? = null

    private var dialog: ProgressDialog? = null

    private var mHandler = Handler() {
        when (it.what) {
            MuseumActivity.MOVE -> {
                if(dialog != null) {
                    dialog!!.dismiss()
                    dialog = null
                }
            }
            MuseumActivity.SUCCESS -> handleSuccess()
            MuseumActivity.FAIL -> handleFail()
        }

        return@Handler true
    }

    private fun handleSuccess() {
        if (dialog != null && dialog!!.isShowing) {
            mHandler.sendMessage(mHandler.obtainMessage(MuseumActivity.MOVE))
        }
    }

    private fun handleFail() {
        if (dialog != null && dialog!!.isShowing) {
            Toast.makeText(this, "Fail to request Type Message", Toast.LENGTH_SHORT).show()
            mHandler.sendMessage(mHandler.obtainMessage(MuseumActivity.MOVE))
        }
    }

    private val mListener = object : SerialInputOutputManager.Listener {
        override fun onRunError(e: Exception) {}

        override fun onNewData(data: ByteArray) {
            this@MuseumActivity.runOnUiThread {
                if (task != null) {
                    task!!.cancel()
                    videoTimer.purge()
                }
                try {
                    rcvdId = TypeChangeUtil.byteToIntId(data)
                    rcvdType = TypeChangeUtil.byteToIntType(data)

                    //For Debugging the VLC data
                    //Toast.makeText(this@MuseumActivity, "recv_id:$rcvdId\nrecv_Type:$rcvdType\nData:${HexDump.dumpHexString(data)}\n", Toast.LENGTH_SHORT).show();

                    //updateReceivedData(data)

                    processVLCdata()

                    task = CountVideoTask()
                    videoTimer.schedule(task, 1000)

                } catch (e : Exception) {
                    Toast.makeText(this@MuseumActivity, "RcvError:$e",  Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /* Response callback Interface */
    interface IReceived {
        fun getResponseBody(msg: String)
    }

    private fun updateContents() {
        when (rcvdType) {
            1 -> {
                contentDesc.visibility = View.VISIBLE
                contentVideo.visibility = View.INVISIBLE
                contents_init.visibility = View.INVISIBLE
            }
            2 -> {
                contentDesc.visibility = View.INVISIBLE
                contentVideo.visibility = View.VISIBLE
                contents_init.visibility = View.INVISIBLE
            }
            else -> {
                contentDesc.visibility = View.INVISIBLE
                contentVideo.visibility = View.INVISIBLE
                contents_init.visibility = View.VISIBLE
                rcvdType = 0
            }
        }
    }

    private fun processVLCdata() {
        updateContents()
        when (rcvdType) {
            1 -> updateImage(rcvdId)
            2 -> try {
                playVideo(rcvdId)
                if (mPlayer != null)
                    mPlayer!!.start()
            } catch (e: Exception) {
                //Toast.makeText(getApplicationContext(), "ProcessError:$e", Toast.LENGTH_SHORT).show()
            }
            else -> Toast.makeText(this, "Not Processing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateReceivedData(data: ByteArray) {
        val message = "Read : " + data.size + " bytes :\n" + HexDump.toHexString(data) + "\n"
        Toast.makeText(applicationContext, "Data:$message", Toast.LENGTH_LONG).show()
    }

    private fun updateImage(id: Int) {
        try {
            var path = Uri.parse(Environment.getExternalStorageDirectory().absolutePath + "/Download" + "/VLC/image_vt" + id + ".jpg")
            //Toast.makeText(this@MuseumActivity, "path:$path", Toast.LENGTH_SHORT).show()
            contentDesc.setImageURI(path)
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Image Error:$e", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playVideo(id: Int) {
        try {
            if(pastId != rcvdId) {
                val path = Environment.getExternalStorageDirectory().absolutePath + "/Download" + "/VLC/video_vt" + id + ".mp4"
                contentVideo.setVideoPath(path)
                contentVideo.requestFocus()
                contentVideo.start()
                pastId = rcvdId
            }
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Video Error:$e", Toast.LENGTH_SHORT).show()
        }
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
        if (sPort != null) {
            mSerialIoManager = SerialInputOutputManager(sPort, mListener)
            mExecutor.submit(mSerialIoManager)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode_museum);

        try {
            sPort = UsbSingleton.getUsbPort()
        } catch (e:Exception) {
            Toast.makeText(this, "Error:$e", Toast.LENGTH_LONG).show()
            finish()
        }

        findViewById<ImageView>(R.id.activity_icon).setImageResource(R.mipmap.icon_museum2)

        this.recyclerView = findViewById(R.id.recycler_view)
        this.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        this.recyclerView.setHasFixedSize(true)

        this.contentDesc = findViewById(R.id.contents_desc)
        this.contentVideo = findViewById(R.id.contents_video)

        this.recyclerView.adapter = this.mAdapter
        this.recyclerView.layoutManager = this.layoutManager

        this.mAdapter.addItem(Content("Description"))
        this.mAdapter.addItem(Content("Video"))
        this.mAdapter.notifyDataSetChanged()

        this.videoTimer = Timer()

        startInitAnimation()

        contentVideo.setOnPreparedListener(MediaPlayer.OnPreparedListener { mp ->
            mPlayer = mp
        })
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

    fun sendMessage(reqType : String) {
        dialog = ProgressDialog(this@MuseumActivity)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        HttpRequestService.getObject().httpRequestWithHandler(this@MuseumActivity, "POST",
                "/cnt-museum", reqType, 4,
                object : HttpResponseEventRouter {
                    override fun route(context: Context, code: Int, arg: String) {
                        runOnUiThread {
                            if (code == 201) {
                                mHandler.sendMessageDelayed(mHandler.obtainMessage(MuseumActivity.SUCCESS), 1000)
                            } else {
                                mHandler.sendMessageDelayed(mHandler.obtainMessage(MuseumActivity.FAIL), 1000)
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

    fun startInitAnimation() {
        startMyAnimation(R.id.illust_phone)
        Handler().postDelayed(Runnable {
            startMyAnimation(R.id.illust_light)
        }, 500)
        startMyAnimation(R.id.illust_char)
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
                bAnim.duration = 200
                bAnim.fillAfter = true
                bAnim.repeatMode = AlphaAnimation.REVERSE
                bAnim.startOffset = 500
                findViewById<View>(id).startAnimation(bAnim)
                findViewById<View>(id).visibility = View.VISIBLE
            }
        }
    }

    inner class CountVideoTask : TimerTask() {
        override fun run() {
            this@MuseumActivity.runOnUiThread {
                try {
                    if (mPlayer != null)
                        mPlayer!!.pause()
                } catch (e: Exception) {
                    //Toast.makeText(applicationContext, "Time out!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

private class ContentListAdapter : RecyclerView.Adapter<ContentListAdapter.ViewHolder> {
    private var context: MuseumActivity
    private var mItems: ArrayList<Content>

    constructor(context: MuseumActivity, mItems: ArrayList<Content>) {
        this.context = context
        this.mItems = mItems
    }

    constructor(context: MuseumActivity) {
        this.context = context
        this.mItems = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_content, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageResource(mItems[position].contentImage)
        holder.textView.text = mItems[position].name
        holder.constraint.setOnClickListener {
            when (mItems[position].name) {
                "Description" -> {
                    context.contents_desc.visibility = View.INVISIBLE
                    context.contents_video.visibility = View.INVISIBLE
                    context.contents_init.visibility = View.VISIBLE
                    context.startInitAnimation()
                    context.sendMessage("image")

                }
                "Video" -> {
                    context.contents_desc.visibility = View.INVISIBLE
                    context.contents_video.visibility = View.INVISIBLE
                    context.contents_init.visibility = View.VISIBLE
                    context.startInitAnimation()
                    context.sendMessage("video")
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun addItem(content: Content) {
        mItems.add(content)
    }

    fun clearItem() {
        mItems.clear()
    }

    private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById(R.id.content_image)
        var textView: TextView = view.findViewById(R.id.content_name)
        var constraint: ConstraintLayout = view.findViewById(R.id.constraint)
    }
}