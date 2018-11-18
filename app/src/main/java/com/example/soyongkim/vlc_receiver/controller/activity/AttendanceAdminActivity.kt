package com.example.soyongkim.vlc_receiver.controller.activity

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView

import com.example.soyongkim.vlc_receiver.R
import com.example.soyongkim.vlc_receiver.controller.component.adapter.PagerAdapter
import com.example.soyongkim.vlc_receiver.view.CustomViewPager

class AttendanceAdminActivity : AppCompatActivity() {

    private val TAG = AttendanceAdminActivity::class.java.simpleName

    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: PagerAdapter
    private lateinit var viewPager: CustomViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode_admin);
        findViewById<ImageView>(R.id.activity_icon).setImageResource(R.mipmap.icon_manager)
        findViewById<TextView>(R.id.app_title).setText("Manager")

        tabLayout = findViewById(R.id.tab_layout)
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_timer))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_list))

        tabLayout.getTabAt(0)?.icon?.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL


        this.viewPager = findViewById(R.id.pager)
        adapter = PagerAdapter(supportFragmentManager, tabLayout.tabCount)

        viewPager.adapter = this.adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tabLayout.getTabAt(tab.position)?.icon?.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tabLayout.getTabAt(tab.position)?.icon?.setColorFilter(Color.parseColor("#E6E6E6"), PorterDuff.Mode.SRC_IN)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun setSwipeable(swipeable: Boolean) {
        viewPager.setSwipeable(swipeable)
    }
}

