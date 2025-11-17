// filePath: dailytaskforbetty/model/TaskEntity.kt
package com.example.dailytaskforbetty.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dailytaskforbetty.model.TaskCycle

// 数据库表实体（与Task分离）
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val isCompleted: Boolean,
    val reward: Int,
    // 存储枚举的字符串名称（如"DAILY"）
    val cycle: String,
    // 时间用Long类型存储（时间戳，便于数据库操作）
    val lastCompletedTime: Long?,
    val nextRefreshTime: Long,
    // 新增字段：任务创建时间（用于排序）
    val createTime: Long = System.currentTimeMillis()
)