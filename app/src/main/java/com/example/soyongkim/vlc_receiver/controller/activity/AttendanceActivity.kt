package com.example.soyongkim.vlc_receiver.controller.activity

import android.content.Context
import android.content.Intent
import com.example.soyongkim.vlc_receiver.R
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.soyongkim.vlc_receiver.model.item.AttendanceUser
import com.example.soyongkim.vlc_receiver.model.item.UsbSingleton
import com.hoho.android.usbserial.driver.UsbSerialPort
import java.util.ArrayList
private var sPort: UsbSerialPort? = null

class AttendanceActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var adapter: CategoryListAdapter = CategoryListAdapter(this)
    private var layoutManager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_attendance)

        findViewById<ImageView>(R.id.activity_icon).setImageResource(R.mipmap.logo_attendance)
        findViewById<TextView>(R.id.activity_title).setText("Attendance System")

        this.recyclerView = findViewById(R.id.recycler_view)
        this.recyclerView.setHasFixedSize(true)

        this.recyclerView.adapter = this.adapter
        this.recyclerView.layoutManager = this.layoutManager

        this.adapter.addItem(AttendanceUser("Manager" , 1))
        this.adapter.addItem(AttendanceUser("Student",  2))
        this.adapter.notifyDataSetChanged()

        try {
            sPort = UsbSingleton.getUsbPort()
            Toast.makeText(this, "Check: " + sPort?.driver?.device?.deviceName, Toast.LENGTH_SHORT).show()
        } catch (e:Exception) {
            Toast.makeText(this, "Error:$e", Toast.LENGTH_LONG).show()
        }


    }

    private fun showAdminAcitivty() {
        try {
            val intent = Intent(this@AttendanceActivity, AttendanceAdminActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            this.startActivity(intent)
            this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } catch (e:Exception) {
            Toast.makeText(this@AttendanceActivity, "Error:$e", Toast.LENGTH_LONG).show()
        }
    }

    private fun showStudentAcitivty() {
        val intent = Intent(this@AttendanceActivity, AttendanceStudentActivity::class.java)
        this.startActivity(intent)
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }



    private class CategoryListAdapter : RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {
        private var context: AttendanceActivity
        private var mItems: ArrayList<AttendanceUser>
        private var lastPosition: Int = -1

        constructor(context: AttendanceActivity, mItems: ArrayList<AttendanceUser>) {
            this.context = context
            this.mItems = mItems
        }

        constructor(context: AttendanceActivity) {
            this.context = context
            this.mItems = ArrayList()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_content, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.imageView.setImageResource(mItems[position].Image)
            holder.textView.text = mItems[position].Name
            holder.constraint.setOnClickListener {
                when (mItems[position].Name) {
                    "Manager" -> {
                        context.showAdminAcitivty()
                    }
                    "Student" -> {
                        context.showStudentAcitivty()
                    }
                }
            }

            setAnimation(holder.cardView, position)
        }

        override fun getItemCount(): Int {
            return mItems.size
        }

        fun addItem(attendanceUser: AttendanceUser) {
            mItems.add(attendanceUser)
        }

        private fun setAnimation(viewToAnimate: View, position: Int) {
            if (position > lastPosition) {
                val animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
                viewToAnimate.startAnimation(animation)
                lastPosition = position
            }
        }


        private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageView: ImageView = view.findViewById(R.id.content_image)
            var textView: TextView = view.findViewById(R.id.content_name)
            var constraint: ConstraintLayout = view.findViewById(R.id.constraint)
            var cardView: CardView = view.findViewById(R.id.card_view)
        }
    }

    companion object {
        fun show(context: Context, port: UsbSerialPort?) {
            sPort = port
            val intent = Intent(context, AttendanceActivity::class.java)
            //intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT and Intent.FLAG_ACTIVITY_SINGLE_TOP)
            context.startActivity(intent)
        }
    }
}