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
import androidx.lifecycle.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import com.example.dailytaskforbetty.model.*
import com.example.dailytaskforbetty.data.*

class TaskViewModel(
    private val rewardDao: RewardDao,
    private val taskDao: TaskDao
) : ViewModel() {
    // 任务列表：从数据库获取并转换为Task对象（替代原有的内存列表）
    val tasks: StateFlow<List<Task>> = taskDao.observeAllTasks()
        .map { entities -> entities.map { it.toTask() } }
        .stateIn<List<Task>>(  // 显式指定类型为List<Task>
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 总奖励状态（累计完成任务的奖励）
    private val _totalReward = MutableStateFlow(0)
    val totalReward: StateFlow<Int> = _totalReward.asStateFlow()

    // 积分历史列表, 直接使用Dao的Flow（数据库数据变化时自动更新UI）
    val rewardHistories: Flow<List<RewardHistory>> = rewardDao.getRewardHistories()

    // 初始化时启动自动刷新检查（每分钟检查一次）
    init {
        startAutoRefreshChecker()
        loadTotalReward()
        loadInitialTasks()
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

    // 从数据库加载总积分
    private fun loadTotalReward() {
        viewModelScope.launch {
            rewardDao.getTotalRewardFlow().collect { savedTotal ->
                _totalReward.value = savedTotal?.amount ?: 0
            }
        }
    }

    // 初始化任务：若数据库为空则插入预设任务
    private fun loadInitialTasks() {
        viewModelScope.launch {
            val existingTasks = taskDao.observeAllTasks().first()  // 获取当前数据库任务
            if (existingTasks.isEmpty()) {
                // 插入预设任务（转换为Entity）
                val initialTasks = listOf(
                    Task(
                        id = "task_drink",
                        title = "喝水",
                        isCompleted = false,
                        reward = 5,
                        cycle = TaskCycle.DAILY,
                        lastCompletedTime = null,
                        nextRefreshTime = calculateNextRefreshTime(TaskCycle.DAILY, null)
                    ),
                    Task(
                        id = "task_exercise",
                        title = "运动",
                        isCompleted = false,
                        reward = 15,
                        cycle = TaskCycle.WEEKLY,
                        lastCompletedTime = null,
                        nextRefreshTime = calculateNextRefreshTime(TaskCycle.WEEKLY, null)
                    ),
                    Task(
                        id = "task_read",
                        title = "阅读",
                        isCompleted = false,
                        reward = 8,
                        cycle = TaskCycle.DAILY,
                        lastCompletedTime = null,
                        nextRefreshTime = calculateNextRefreshTime(TaskCycle.DAILY, null)
                    )
                ).map { it.toEntity() }  // 转换为Entity

                taskDao.upsertTasks(initialTasks)  // 批量插入数据库
            }
        }
    }

    // 检查并刷新任务（基于当前北京时间）
    fun checkAndRefreshTasks() {
        val currentTime = getBeijingTime()
        viewModelScope.launch {
            val updatedTasks = tasks.value.map { task ->
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
            // 同步更新到数据库
            taskDao.upsertTasks(updatedTasks.map { it.toEntity() })
        }
    }

    // 完成任务：更新状态并计算下次刷新时间
    fun completeTask(taskId: String) {
        val currentTime = getBeijingTime()
        viewModelScope.launch {
            val targetTask = tasks.value.find { it.id == taskId && !it.isCompleted }
            if (targetTask != null) {
                // 1. 更新积分
                val newTotal = _totalReward.value + targetTask.reward
                _totalReward.value = newTotal
                rewardDao.insertOrReplaceTotalReward(TotalReward(amount = newTotal))

                // 2. 记录积分历史
                val timeStr = formatTime(currentTime)
                val history = RewardHistory(
                    type = "获得",
                    amount = targetTask.reward,
                    reason = "完成任务：${targetTask.title}",
                    time = timeStr
                )
                rewardDao.insertRewardHistory(history)

                // 3. 更新任务状态并同步到数据库
                val updatedTask = targetTask.copy(
                    isCompleted = true,
                    lastCompletedTime = currentTime,
                    nextRefreshTime = calculateNextRefreshTime(targetTask.cycle, currentTime)
                )
                taskDao.upsertTask(updatedTask.toEntity())  // 单个任务更新
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
            tasks.value.find { it.id == taskId }?.let {
                taskDao.deleteTask(it.toEntity())  // 转换为Entity后删除
            }
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