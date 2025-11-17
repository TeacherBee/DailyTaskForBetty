package com.example.dailytaskforbetty.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dailytaskforbetty.model.PrizeStatus

// 已兑换奖品的数据库实体
@Entity(tableName = "redeemed_prizes")
data class RedeemedPrizeEntity(
    @PrimaryKey val id: String,
    val productName: String,
    val productPrice: Int,
    val status: String, // 存储PrizeStatus的字符串形式
    val redeemTime: String
)

// 转换方法：RedeemedPrize ←→ RedeemedPrizeEntity
fun RedeemedPrize.toEntity(): RedeemedPrizeEntity {
    return RedeemedPrizeEntity(
        id = id,
        productName = productName,
        productPrice = productPrice,
        status = status.name, // 枚举转字符串
        redeemTime = redeemTime
    )
}

fun RedeemedPrizeEntity.toRedeemedPrize(): RedeemedPrize {
    return RedeemedPrize(
        id = id,
        productName = productName,
        productPrice = productPrice,
        status = PrizeStatus.valueOf(status), // 字符串转枚举
        redeemTime = redeemTime
    )
}