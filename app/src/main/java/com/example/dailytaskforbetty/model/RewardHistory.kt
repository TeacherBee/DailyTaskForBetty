package com.example.dailytaskforbetty.model

// 积分历史记录（获得/消耗积分的原因和时间）
data class RewardHistory(
    val id: String,
    val type: String, // "获得" 或 "消耗"
    val amount: Int, // 积分数量
    val reason: String, // 原因（如“完成任务：买牛奶”或“兑换：笔记本”）
    val time: String // 时间（格式：yyyy-MM-dd HH:mm）
)