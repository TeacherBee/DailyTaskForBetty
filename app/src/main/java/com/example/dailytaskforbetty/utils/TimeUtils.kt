// utils/TimeUtils.kt
package com.example.dailytaskforbetty.utils

import com.example.dailytaskforbetty.model.*
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

object TimeUtils {
    // 公共函数，供所有类调用
    fun calculateNextRefreshTime(cycle: TaskCycle, lastCompletedTime: Date?): Date {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"))
        calendar.time = lastCompletedTime ?: Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai")).time

        return when (cycle) {
            TaskCycle.DAILY -> {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.time
            }
            TaskCycle.THREE_DAYS -> { // 新增：每三天刷新逻辑
                calendar.add(Calendar.DAY_OF_YEAR, 3) // 累加3天
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.time
            }
            TaskCycle.WEEKLY -> {
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.time
            }
        }
    }

    // 商品周期计算
    fun calculateNextRefreshTime(cycle: StockRefreshCycle, lastRefreshTime: Date?): Date {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"))
        calendar.time = lastRefreshTime ?: Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai")).time

        return when (cycle) {
            StockRefreshCycle.DAILY -> {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.time
            }
            StockRefreshCycle.THREE_DAYS -> { // 三天刷新逻辑
                calendar.add(Calendar.DAY_OF_YEAR, 3)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.time
            }
            StockRefreshCycle.WEEKLY -> {
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.time
            }
            StockRefreshCycle.NONE -> calendar.time
        }
    }
}