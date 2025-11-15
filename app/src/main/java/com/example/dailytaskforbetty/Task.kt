// 任务类

package com.example.dailytaskforbetty

// 任务数据类：id（唯一标识）、标题、是否完成
data class Task(
    val id: String,
    val title: String,
    val isCompleted: Boolean
)