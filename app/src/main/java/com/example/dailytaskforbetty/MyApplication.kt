package com.example.dailytaskforbetty

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.example.dailytaskforbetty.data.AppDatabase
import com.example.dailytaskforbetty.viewmodel.*


class MyApplication : Application() {
    // 全局数据库实例
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this) // 关键：初始化ThreeTenABP时区数据
        // 初始化数据库
        database = AppDatabase.getDatabase(this)
        // 启动任务刷新的周期性工作
        setupTaskRefreshWork(this)
        // 初始化库存刷新
        setupStockRefreshWork(this)
    }
}