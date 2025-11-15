package com.example.dailytaskforbetty

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this) // 关键：初始化ThreeTenABP时区数据
    }
}