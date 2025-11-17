// 任务栏类

package com.example.dailytaskforbetty.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID // 用于生成唯一ID
import kotlin.random.Random
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.plus
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import com.example.dailytaskforbetty.model.*
import com.example.dailytaskforbetty.data.*

class TaskViewModel(private val rewardDao: RewardDao) : ViewModel() {
    // 预设任务列表（初始化时计算下次刷新时间）
    private val _tasks = MutableStateFlow<List<Task>>(
        listOf(
            // 任务1：喝水（每天刷新，奖励5积分）
            Task(
                id = "task_drink",
                title = "喝水",
                isCompleted = false,
                reward = 5,
                cycle = TaskCycle.DAILY,
                lastCompletedTime = null,
                nextRefreshTime = calculateNextRefreshTime(TaskCycle.DAILY, null)
            ),
            // 任务2：运动（每周刷新，奖励15积分）
            Task(
                id = "task_exercise",
                title = "运动",
                isCompleted = false,
                reward = 15,
                cycle = TaskCycle.WEEKLY,
                lastCompletedTime = null,
                nextRefreshTime = calculateNextRefreshTime(TaskCycle.WEEKLY, null)
            ),
            // 可添加更多预设任务...
            Task(
                id = "task_read",
                title = "阅读",
                isCompleted = false,
                reward = 8,
                cycle = TaskCycle.DAILY,
                lastCompletedTime = null,
                nextRefreshTime = calculateNextRefreshTime(TaskCycle.DAILY, null)
            )
        )
    )
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    // 总奖励状态（累计完成任务的奖励）
    private val _totalReward = MutableStateFlow(0)
    val totalReward: StateFlow<Int> = _totalReward.asStateFlow()

    // 积分历史列表, 直接使用Dao的Flow（数据库数据变化时自动更新UI）
    val rewardHistories: Flow<List<RewardHistory>> = rewardDao.getRewardHistories()

    // 初始化时启动自动刷新检查（每分钟检查一次）
    init {
        startAutoRefreshChecker()
        // 从数据库加载总积分
        viewModelScope.launch {
            // 调用Dao的getTotalRewardFlow()获取Flow，然后collect（收集）它
            rewardDao.getTotalRewardFlow().collect { savedTotal ->
                // 每当数据库中总积分变化时，这里会自动触发
                _totalReward.value = savedTotal?.amount ?: 0 // 赋值给状态流
            }
        }
    }

    // 自动刷新检查：每分钟检查一次是否有任务需要刷新
    private fun startAutoRefreshChecker() {
        viewModelScope.launch {
            // 每分钟触发一次检查（60秒）
            flow {
                while (true) {
                    emit(Unit)
                    delay(60_000) // 60秒延迟
                }
            }.collect {
                checkAndRefreshTasks()
            }
        }
    }

    // 检查并刷新任务（基于当前北京时间）
    fun checkAndRefreshTasks() {
        val currentTime = getBeijingTime() // 获取当前北京时间
        viewModelScope.launch {
            _tasks.value = _tasks.value.map { task ->
                // 若当前时间已过下次刷新时间，重置任务状态
                if (currentTime.after(task.nextRefreshTime)) {
                    task.copy(
                        isCompleted = false,
                        lastCompletedTime = null,
                        nextRefreshTime = calculateNextRefreshTime(task.cycle, null)
                    )
                } else {
                    task
                }
            }
        }
    }

    // 完成任务：更新状态并计算下次刷新时间
    fun completeTask(taskId: String) {
        val currentTime = getBeijingTime()
        viewModelScope.launch {
            _tasks.value = _tasks.value.map { task ->
                if (task.id == taskId && !task.isCompleted) {
                    // 1. 计算新积分
                    val newTotal = _totalReward.value + task.reward
                    _totalReward.value = newTotal

                    // 2. 保存总积分到数据库（新增）
                    val totalEntity = TotalReward(amount = newTotal)
                    rewardDao.insertOrReplaceTotalReward(totalEntity)

                    // 3. 记录积分历史到数据库（新增）
                    val timeStr = formatTime(currentTime)
                    val history = RewardHistory(
                        type = "获得",
                        amount = task.reward,
                        reason = "完成任务：${task.title}",
                        time = timeStr
                    )
                    rewardDao.insertRewardHistory(history)

                    // 4. 更新任务状态：标记完成，记录完成时间，计算下次刷新
                    task.copy(
                        isCompleted = true,
                        lastCompletedTime = currentTime,
                        nextRefreshTime = calculateNextRefreshTime(task.cycle, currentTime)
                    )
                } else {
                    task
                }
            }
        }
    }

    // 计算下次刷新时间（核心逻辑）
    private fun calculateNextRefreshTime(cycle: TaskCycle, lastCompletedTime: Date?): Date {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai")) // 北京时间
        calendar.time = lastCompletedTime ?: getBeijingTime() // 若未完成过，基于当前时间计算

        return when (cycle) {
            TaskCycle.DAILY -> {
                // 每天刷新：下次刷新为明天0点
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.time
            }
            TaskCycle.WEEKLY -> {
                // 每周刷新：下次刷新为下周同一天0点
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.time
            }
        }
    }

    // 工具函数：获取当前北京时间
    private fun getBeijingTime(): Date {
        return Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai")).time
    }

    // 工具函数：格式化时间为字符串（yyyy-MM-dd HH:mm）
    private fun formatTime(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).apply {
            timeZone = TimeZone.getTimeZone("Asia/Shanghai")
        }.format(date)
    }

    // 删除任务（根据ID过滤掉要删除的任务）
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            _tasks.value = _tasks.value.filter { it.id != taskId }
        }
    }

    // 减少总奖励（供商店兑换使用）
    fun reduceReward(amount: Int, productName: String) {
        viewModelScope.launch {
            val newTotal = _totalReward.value - amount
            _totalReward.value = newTotal

            // 直接插入或替换（无需判断）
            val totalEntity = TotalReward(amount = newTotal)
            rewardDao.insertOrReplaceTotalReward(totalEntity)

            // 记录积分消耗历史到数据库
            val timeStr = formatTime(getBeijingTime())
            val history = RewardHistory(
                type = "消耗",
                amount = amount,
                reason = "兑换：$productName",
                time = timeStr
            )
            rewardDao.insertRewardHistory(history)
        }
    }
}