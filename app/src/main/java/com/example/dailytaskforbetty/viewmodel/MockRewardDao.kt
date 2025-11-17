package com.example.dailytaskforbetty.viewmodel

import com.example.dailytaskforbetty.data.*
import com.example.dailytaskforbetty.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

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