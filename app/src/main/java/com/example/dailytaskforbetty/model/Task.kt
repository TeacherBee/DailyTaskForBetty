// 任务类

package com.example.dailytaskforbetty.model

import java.util.Date

// 任务数据类：id（唯一标识）、标题、是否完成
data class Task(
    val id: String,
    val title: String,
    val isCompleted: Boolean,
    val reward: Int, // 奖励值
    val cycle: TaskCycle,    // 刷新周期（每天/每周）
    var lastCompletedTime: Date?, // 上次完成时间（用于计算下次刷新）
    var nextRefreshTime: Date   // 下次刷新时间
)