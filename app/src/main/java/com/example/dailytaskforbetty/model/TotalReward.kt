package com.example.dailytaskforbetty.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "total_reward")
data class TotalReward(
    @PrimaryKey val id: Int = 1,
    val amount: Int = 0
) {
    // 显式定义无参构造函数，并标记@Ignore，告诉Room忽略它
    @Ignore
    constructor() : this(1, 0)
}