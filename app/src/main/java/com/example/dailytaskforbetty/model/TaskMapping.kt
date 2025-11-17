// filePath: dailytaskforbetty/model/TaskMapping.kt
package com.example.dailytaskforbetty.model

import java.util.Date

// TaskEntity转Task（数据库实体→内存模型）
fun TaskEntity.toTask(): Task {
    return Task(
        id = id,
        title = title,
        isCompleted = isCompleted,
        reward = reward,
        cycle = TaskCycle.valueOf(cycle), // 字符串转枚举
        lastCompletedTime = lastCompletedTime?.let { Date(it) }, // 时间戳转Date
        nextRefreshTime = Date(nextRefreshTime)
    )
}

// Task转TaskEntity（内存模型→数据库实体）
fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        isCompleted = isCompleted,
        reward = reward,
        cycle = cycle.name, // 枚举转字符串
        lastCompletedTime = lastCompletedTime?.time, // Date转时间戳
        nextRefreshTime = nextRefreshTime.time
    )
}