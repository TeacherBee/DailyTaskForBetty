package com.example.dailytaskforbetty.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dailytaskforbetty.data.AppDatabase
import com.example.dailytaskforbetty.MyApplication

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = (context.applicationContext as MyApplication).database

        // 支持TaskViewModel
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(
                database.rewardDao(),
                taskDao = database.taskDao()
            ) as T
        }
        // 新增：支持ShopViewModel
        else if (modelClass.isAssignableFrom(ShopViewModel::class.java)) {
            return ShopViewModel(
                redeemedPrizeDao = database.redeemedPrizeDao()
            ) as T
        }

        throw IllegalArgumentException("未知的ViewModel类")
    }
}