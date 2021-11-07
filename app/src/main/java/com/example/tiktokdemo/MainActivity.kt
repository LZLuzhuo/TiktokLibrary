package com.example.tiktokdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tiktokdemo.tiktok.TiktokActivityDemo

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun activity(view: android.view.View) {
         TiktokActivityDemo.start(this)
//        ViewPagerActivity.start(this)
    }
}