package com.example.dailytaskforbetty.viewmodel

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.*
import java.util.concurrent.TimeUnit
import com.example.dailytaskforbetty.data.*
import com.example.dailytaskforbetty.model.*
import com.example.dailytaskforbetty.viewmodel.*
import com.example.dailytaskforbetty.utils.TimeUtils

class TaskRefreshWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val taskDao = database.taskDao()
        val currentTime = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai")).time

        // 执行刷新逻辑
        val tasks = taskDao.observeAllTasks().first().map { it.toTask() }
        val updatedTasks = tasks.map { task ->
            if (currentTime.after(task.nextRefreshTime)) {
                task.copy(
                    isCompleted = false,
                    lastCompletedTime = null,
                    nextRefreshTime = TimeUtils.calculateNextRefreshTime(task.cycle, null)
                )
            } else {
                task
            }
        }
        taskDao.upsertTasks(updatedTasks.map { it.toEntity() })
        return Result.success()
    }
}

// 3. 在 Application 或 ViewModel 中启动周期性任务
fun setupTaskRefreshWork(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED) // 不需要网络
        .build()

    val refreshRequest = PeriodicWorkRequestBuilder<TaskRefreshWorker>(1, TimeUnit.HOURS)
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            "task_refresh",
            ExistingPeriodicWorkPolicy.REPLACE,
            refreshRequest
        )
}