package com.example.tiktokdemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktokdemo.tiktok.TikTokFragmentDemo
import kotlinx.android.synthetic.main.activity_viewpager.*
import me.luzhuo.lib_core.ui.adapter.ViewPagerAdapter

class ViewPagerActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ViewPagerActivity::class.java))
        }
    }

    val fragments = arrayListOf(
        ViewPagerAdapter.ViewPagerBean(FragmentTest(), "ABC"),
        ViewPagerAdapter.ViewPagerBean(FragmentTest(), "ABC"),
        ViewPagerAdapter.ViewPagerBean(FragmentTest(), "ABC"),
        ViewPagerAdapter.ViewPagerBean(TikTokFragmentDemo.instance(), "Video"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewpager)

        viewpager.adapter = ViewPagerAdapter(this, fragments)
        viewpager.currentItem = 1
        viewpager.offscreenPageLimit = fragments.size
        tablayout.setupWithViewPager(viewpager)
    }
}