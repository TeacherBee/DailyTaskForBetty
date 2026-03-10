// 任务类

package com.example.dailytaskforbetty.model

import java.util.Date

// 任务数据类：id（唯一标识）、标题、是否完成
data class Task(
    val id: String,
    val title: String,
    val isCompleted: Boolean,// 当日是否完成
    val reward: Int, // 奖励值
    val cycle: TaskCycle,    // 刷新周期（每天/每周）
    var lastCompletedTime: Date?, // 上次完成时间（用于计算下次刷新）
    var nextRefreshTime: Date,   // 下次刷新时间
    // 每周目标完成次数（调整为5次）
    val weeklyTarget: Int = 5,
    // 本周已完成次数
    var weeklyCompletedCount: Int = 0
)