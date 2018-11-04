package com.example.soyongkim.vlc_receiver.controller.fragment

import com.example.soyongkim.vlc_receiver.R
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.soyongkim.vlc_receiver.model.item.Student
import java.util.ArrayList

class TabListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryListAdapter
    private lateinit var layoutManager : LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view : View = inflater.inflate(R.layout.fragment_admin_list, container, false)

        this.adapter = CategoryListAdapter(this)
        this.layoutManager = LinearLayoutManager(activity)

        this.recyclerView = view.findViewById(R.id.recycler_view)
        this.recyclerView.setHasFixedSize(true)

        this.recyclerView.adapter = this.adapter
        this.recyclerView.layoutManager = this.layoutManager

        this.adapter.addItem(Student("Soyong", "2018220889"))
        this.adapter.addItem(Student("Aaron", "2016116545"))
        this.adapter.addItem(Student("Donald", "2016113067"))
        this.adapter.addItem(Student("Gabriel", "2016112530"))
        this.adapter.addItem(Student("Martin", "2014105004"))
        this.adapter.addItem(Student("Michael", "2013097010"))
        this.adapter.addItem(Student("Robert", "2013105016"))
        this.adapter.addItem(Student("Sabastian", "2014105019"))
        this.adapter.addItem(Student("Wallace", "2014105022"))

        this.adapter.notifyDataSetChanged()

        return view;
    }

    private class CategoryListAdapter : RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {
        private var context: TabListFragment
        private var mItems: ArrayList<Student>
        private var lastPosition: Int = -1

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

//            if(mItems[position].Name == "Display"){}
//            else if(mItems[position].PermissionLevel >= permissionLevel) {
//                holder.status_green.setBackgroundResource(R.drawable.bg_circle_green_fill)
//            }
//            else{
//                holder.status_red.setBackgroundResource(R.drawable.bg_circle_red_fill)
//            }

            holder.studentImage.setImageResource(mItems[position].Image)
            holder.checkImage.setImageResource(mItems[position].cateImage)
            holder.studentName.text = mItems[position].Name
            holder.studentNumber.text = mItems[position].StudentNum
            holder.constraint.setOnClickListener {
            }
        }

        override fun getItemCount(): Int {
            return mItems.size
        }

        fun addItem(student: Student) {
            mItems.add(student)
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