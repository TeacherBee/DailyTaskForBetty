// filePath: dailytaskforbetty/model/TaskDao.kt
package com.example.dailytaskforbetty.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import com.example.dailytaskforbetty.model.*

// 数据库操作接口
@Dao
interface TaskDao {
    // 监听所有任务变化（返回Flow，自动更新UI）
    @Query("SELECT * FROM tasks ORDER BY createTime ASC")
    fun observeAllTasks(): Flow<List<TaskEntity>>

    // 插入或更新任务（存在则更新，不存在则插入）
    @Upsert
    suspend fun upsertTask(task: TaskEntity)

    // 批量插入或更新任务
    @Upsert
    suspend fun upsertTasks(tasks: List<TaskEntity>)

    // 删除任务
    @Delete
    suspend fun deleteTask(task: TaskEntity)

    // 删除所有任务（可选）
    @Query("DELETE FROM tasks")
    suspend fun clearAllTasks()
}