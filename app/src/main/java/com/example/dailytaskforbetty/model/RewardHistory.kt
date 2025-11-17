// RewardHistory.kt
package com.example.dailytaskforbetty.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "reward_history")
data class RewardHistory(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val type: String, // "获得" 或 "消耗"
    val amount: Int, // 积分数量
    val reason: String,
    val time: String // 格式：yyyy-MM-dd HH:mm
)