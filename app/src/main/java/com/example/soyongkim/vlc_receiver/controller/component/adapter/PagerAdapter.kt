package com.example.soyongkim.vlc_receiver.controller.component.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import com.example.soyongkim.vlc_receiver.controller.activity.AttendanceAdminActivity
import com.example.soyongkim.vlc_receiver.controller.fragment.TabListFragment
import com.example.soyongkim.vlc_receiver.controller.fragment.TabMainFragment
import com.example.soyongkim.vlc_receiver.model.item.Student
import java.util.ArrayList

class PagerAdapter constructor(fm: FragmentManager, internal var mNumOfTabs: Int) : FragmentStatePagerAdapter(fm){

    override fun getItem(position: Int): Fragment? {

        when (position) {
            0 -> {
                return TabMainFragment()
            }
            1 -> {
                return TabListFragment()
            }
            else -> return null
        }
    }

    override fun getCount(): Int {
        return mNumOfTabs
    }
}