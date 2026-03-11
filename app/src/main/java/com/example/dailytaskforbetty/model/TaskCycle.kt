package com.example.dailytaskforbetty.model

// 任务刷新周期：每天/每周
enum class TaskCycle {
    DAILY,   // 每天刷新
    THREE_DAYS, // 每三天刷新
    WEEKLY,   // 每周刷新
    WEEKLY_5_TIMES,  // 每周5次（新增）
    SUNDAY_ONLY  // 仅周日可以完成
}