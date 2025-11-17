package com.example.dailytaskforbetty

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.example.dailytaskforbetty.data.AppDatabase

class MyApplication : Application() {
    // 全局数据库实例
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this) // 关键：初始化ThreeTenABP时区数据
        // 初始化数据库
        database = AppDatabase.getDatabase(this)
    }
}