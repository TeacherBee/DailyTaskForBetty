package com.example.dailytaskforbetty.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "red_packet_history")
data class RedPacketHistoryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val type: String, // "收入" 或 "提现"
    val amount: Double, // 金额，单位：元
    val reason: String, // 原因描述
    val time: String // 时间格式：yyyy-MM-dd HH:mm
)