package com.example.soyongkim.vlc_receiver.controller.fragment

import android.content.Context
import com.example.soyongkim.vlc_receiver.R
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.soyongkim.vlc_receiver.controller.util.HttpResponseEventRouter
import com.example.soyongkim.vlc_receiver.model.item.Student
import com.example.soyongkim.vlc_receiver.model.service.HttpRequestService
import com.example.soyongkim.vlc_receiver.view.ProgressDialog
import org.json.JSONArray
import java.util.*

class TabListFragment : Fragment() {

    companion object {
        const val MOVE = 0
        const val GET = 1
        const val POST = 2
        const val FAIL = 3
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryListAdapter
    private lateinit var layoutManager : LinearLayoutManager
    private lateinit var jsonArray : JSONArray
    private var dialog: ProgressDialog? = null

    private var mHandler = Handler() {
        when (it.what) {
            TabListFragment.MOVE -> {
                if(dialog != null) {
                    dialog!!.dismiss()
                    dialog = null
                }
            }
            TabListFragment.GET -> handleSuccess("GET")
            TabListFragment.POST -> handleSuccess("POST")
            TabListFragment.FAIL -> handleFail()
        }

        return@Handler true
    }

    private fun handleSuccess(req : String) {
        if (dialog != null && dialog!!.isShowing) {
        }

        mHandler.sendMessage(mHandler.obtainMessage(TabListFragment.MOVE))

    }

    private fun handleFail() {
        Toast.makeText(context, "Fail to load", Toast.LENGTH_SHORT).show()
        if (dialog != null && dialog!!.isShowing) {
            mHandler.sendMessage(mHandler.obtainMessage(TabListFragment.MOVE))
        }
    }

    private fun loadingList() {
        dialog = ProgressDialog(context!!)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
        HttpRequestService.getObject().httpRequestWithHandler(context!!, "GET",
                "/list_student",
                object : HttpResponseEventRouter {
                    override fun route(context: Context, code: Int, arg: String) {
                        activity!!.runOnUiThread {
                            if (code == 200) {
                                //Toast.makeText(context, arg, Toast.LENGTH_SHORT).show()
                                jsonArray = org.json.JSONArray(arg)
                                updateList()
                                mHandler.sendMessageDelayed(mHandler.obtainMessage(TabListFragment.GET), 1000)
                            } else {
                                //Toast.makeText(context, arg, Toast.LENGTH_SHORT).show()
                                mHandler.sendMessageDelayed(mHandler.obtainMessage(TabListFragment.FAIL), 1000)
                            }
                        }

                    }
                })

        Timer().schedule(object : TimerTask() {
            override fun run() {
                activity!!.runOnUiThread {
                    dialog!!.show()
                }
            }
        }, 200)
    }

    private fun requestState(sid : String, state : String){
        dialog = ProgressDialog(context!!)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)

        var query : String
        if(state == "x")
            query = "o"
        else
            query = "x"
        var resource = "/std_$sid/cnt-state"

        HttpRequestService.getObject().httpRequestWithHandler(context!!, "POST",
                resource, query , 4,
                object : HttpResponseEventRouter {
                    override fun route(context: Context, code: Int, arg: String) {
                        activity!!.runOnUiThread {
                            if(code == 201 || code == 202)
                                mHandler.sendMessageDelayed(mHandler.obtainMessage(TabListFragment.POST), 600)
                            else
                                mHandler.sendMessageDelayed(mHandler.obtainMessage(TabListFragment.FAIL), 600)
                        }
                    }
                })
        Timer().schedule(object : TimerTask() {
            override fun run() {
                activity!!.runOnUiThread {
                    dialog!!.show()
                }
            }
        }, 200)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view : View = inflater.inflate(R.layout.fragment_admin_list, container, false)

        this.adapter = CategoryListAdapter(this)
        this.layoutManager = LinearLayoutManager(activity)

        this.recyclerView = view.findViewById(R.id.recycler_view)
        this.recyclerView.setHasFixedSize(true)

        this.recyclerView.adapter = this.adapter
        this.recyclerView.layoutManager = this.layoutManager

        loadingList()

        return view
    }

    private fun updateList() {
        this.adapter.clearItem()
        for(i in 0 until jsonArray.length()) {
            var jObject = jsonArray.getJSONObject(i)
            this.adapter.addItem(Student(jObject.getString("name"), jObject.getString("sid"), jObject.getString("state")))
        }
        this.adapter.notifyDataSetChanged()
    }

    private class CategoryListAdapter : RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {
        private var context: TabListFragment
        private var mItems: ArrayList<Student>

        constructor(context: TabListFragment, mItems: ArrayList<Student>) {
            this.context = context
            this.mItems = mItems
        }

        constructor(context: TabListFragment) {
            this.context = context
            this.mItems = ArrayList()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.studentImage.setImageResource(mItems[position].Image)
            holder.checkImage.setImageResource(mItems[position].checkImage)
            holder.studentName.text = mItems[position].Name
            holder.studentNumber.text = mItems[position].StudentNum
            holder.constraint.setOnClickListener {
                context.requestState(mItems[position].StudentNum, mItems[position].state)
                if(mItems[position].state == "o")
                    mItems[position].state = "x"
                else
                    mItems[position].state = "o"

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        holder.checkImage.setImageResource(mItems[position].checkImage)
                    }
                }, 1000)
            }
        }

        override fun getItemCount(): Int {
            return mItems.size
        }

        fun addItem(student: Student) {
            mItems.add(student)
        }


        fun clearItem() {
            mItems.clear()
        }

        private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var checkImage: ImageView = view.findViewById(R.id.cate_image)
            var studentImage: ImageView = view.findViewById(R.id.student_image)
            var studentName: TextView = view.findViewById(R.id.txt_studentName)
            var studentNumber: TextView = view.findViewById(R.id.txt_studentNumber)
            var constraint: ConstraintLayout = view.findViewById(R.id.constraint)
        }
    }
}