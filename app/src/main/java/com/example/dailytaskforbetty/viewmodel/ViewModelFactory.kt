package com.example.dailytaskforbetty.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dailytaskforbetty.data.AppDatabase
import com.example.dailytaskforbetty.MyApplication

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            // 从Application中获取数据库实例，再获取Dao
            val database = (context.applicationContext as MyApplication).database
            return TaskViewModel(database.rewardDao()) as T
        }
        throw IllegalArgumentException("未知的ViewModel类")
    }
}