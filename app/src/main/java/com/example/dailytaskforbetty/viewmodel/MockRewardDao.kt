package com.example.dailytaskforbetty.viewmodel

import com.example.dailytaskforbetty.data.*
import com.example.dailytaskforbetty.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Date

class MockRewardDao : RewardDao {
    // 模拟总积分Flow（返回100作为示例）
    override fun getTotalRewardFlow(): Flow<TotalReward?> {
        return flowOf(TotalReward(amount = 100))
    }

    // 模拟插入或替换总积分（空实现）
    override suspend fun insertOrReplaceTotalReward(totalReward: TotalReward) {}

    // 模拟积分历史Flow（返回假数据）
    override fun getRewardHistories(): Flow<List<RewardHistory>> {
        return flowOf(emptyList()) // 预览时默认空列表
    }

    // 模拟插入积分历史（空实现）
    override suspend fun insertRewardHistory(history: RewardHistory) {}
}

class MockTaskDao : TaskDao {
    // 模拟返回预设任务
    override fun observeAllTasks(): Flow<List<TaskEntity>> {
        val mockTime = System.currentTimeMillis()
        val mockEntities = listOf(
            Task(
                id = "mock1",
                title = "喝水",
                isCompleted = false,
                reward = 5,
                cycle = TaskCycle.DAILY,
                lastCompletedTime = null,
                nextRefreshTime = Date(mockTime + 86400000) // 24小时后刷新
            ).toEntity(),
            Task(
                id = "mock2",
                title = "运动",
                isCompleted = false,
                reward = 15,
                cycle = TaskCycle.WEEKLY,
                lastCompletedTime = null,
                nextRefreshTime = Date(mockTime + 604800000) // 7天后刷新
            ).toEntity()
        )
        return flowOf(mockEntities)
    }

    // 其他方法模拟实现（空实现即可，预览不依赖实际存储）
    override suspend fun upsertTask(task: TaskEntity) {}
    override suspend fun upsertTasks(tasks: List<TaskEntity>) {}
    override suspend fun deleteTask(task: TaskEntity) {}
    override suspend fun clearAllTasks() {}
}