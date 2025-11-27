package com.example.dailytaskforbetty.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Ignore

@Entity(tableName = "red_packet_balance")
data class RedPacketBalanceEntity(
    @PrimaryKey val id: Int = 1,
    val balance: Double = 0.0 // 红包余额，单位：元
) {
    @Ignore
    constructor() : this(1, 0.0)
}