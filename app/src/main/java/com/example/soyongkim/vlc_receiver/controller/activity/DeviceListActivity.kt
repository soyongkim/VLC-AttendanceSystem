package com.example.soyongkim.vlc_receiver.controller.activity

import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import com.example.soyongkim.vlc_receiver.R
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import java.util.ArrayList
import com.example.soyongkim.vlc_receiver.model.item.*
class DeviceListActivity : AppCompatActivity() {

    private val TAG = DeviceListActivity::class.java.simpleName

    private lateinit var recyclerView: RecyclerView
    private val mEntries = ArrayList<UsbSerialPort>()
    private var mAdapter: DeviceListAdapter = DeviceListAdapter(this)
    private var layoutManager = LinearLayoutManager(this)
    internal lateinit var intent: Intent

    private var mUsbManager: UsbManager? = null
    private var mProgressBar: ProgressBar? = null

    private val MESSAGE_REFRESH = 101
    private val REFRESH_TIMEOUT_MILLIS: Long = 5000

    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_REFRESH -> {
                    refreshDeviceList()
                    this.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS)
                }
                else -> super.handleMessage(msg)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_device);

        this.recyclerView = findViewById(R.id.recycler_view)
        this.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        this.recyclerView.setHasFixedSize(true)

        mUsbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        mProgressBar = findViewById(R.id.progressBar)

        this.recyclerView.adapter = this.mAdapter
        this.recyclerView.layoutManager = this.layoutManager
    }

    override fun onResume() {
        super.onResume()
        mHandler.sendEmptyMessage(MESSAGE_REFRESH)
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeMessages(MESSAGE_REFRESH)
    }

    private fun refreshDeviceList() {
        showProgressBar()

        object : AsyncTask<Void, Void, List<UsbSerialPort>>() {
            override fun doInBackground(vararg params: Void): List<UsbSerialPort> {
                Log.d(TAG, "Refreshing device list ...")
                SystemClock.sleep(1000)

                val drivers:List<UsbSerialDriver>? = UsbSerialProber.getDefaultProber()?.findAllDrivers(mUsbManager)
                val result = ArrayList<UsbSerialPort>()

                for (driver in drivers.orEmpty()) {
                    val ports = driver.ports
                    Log.d(TAG, String.format("+ %s: %s port%s",
                            driver, Integer.valueOf(ports.size), if (ports.size == 1) "" else "s"))
                    result.addAll(ports)
                }

                return result
            }

            override fun onPostExecute(result: List<UsbSerialPort>) {
                mEntries.clear()
                mEntries.addAll(result)
                insertPorts()
                mAdapter.notifyDataSetChanged()
                hideProgressBar()
                Log.d(TAG, "Done refreshing, " + mEntries.size + " entries found.")
            }

        }.execute(null as Void?)
    }

    private fun insertPorts() {
        mAdapter.clearItem()
        for(item in mEntries) {
            mAdapter.addItem(UsbSerialPortItem(item))
        }
    }

    private fun showProgressBar() {
        mProgressBar?.setVisibility(View.VISIBLE)
    }

    private fun hideProgressBar() {
        mProgressBar?.setVisibility(View.INVISIBLE)
    }

}


private class DeviceListAdapter : RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {
    private var context: DeviceListActivity
    private var mItems: ArrayList<UsbSerialPortItem>
    private var lastPosition: Int = -1

    constructor(context: DeviceListActivity, mItems: ArrayList<UsbSerialPortItem>) {
        this.context = context
        this.mItems = mItems
    }

    constructor(context: DeviceListActivity) {
        this.context = context
        this.mItems = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageUsb.setImageResource(mItems[position].usbimage)
        holder.textDevice.text = mItems[position].device.productName
        holder.textProduct.text = mItems[position].device.manufacturerName
        holder.constraint.setOnClickListener {
            try {
                SelectModeActivity.show(this.context, mItems[position].port)
            } catch (e:Exception) {
                Toast.makeText(context, "Error:$e",Toast.LENGTH_SHORT).show()
            }
        }
        setAnimation(holder.cardView, position)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun addItem(content: UsbSerialPortItem) {
        mItems.add(content)
    }

    fun clearItem() {
        mItems.clear()
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // 새로 보여지는 뷰라면 애니메이션을 해줍니다
        if (itemCount != lastPosition) {
            val animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = itemCount
        }
    }

    private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageUsb: ImageView = view.findViewById(R.id.usb_image)
        var textDevice: TextView = view.findViewById(R.id.usb_device_name)
        var textProduct: TextView = view.findViewById(R.id.usb_product_name)
        var constraint: ConstraintLayout = view.findViewById(R.id.constraint)
        var cardView: CardView = view.findViewById(R.id.card_view)
    }
}