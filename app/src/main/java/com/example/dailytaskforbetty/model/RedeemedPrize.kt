package com.example.dailytaskforbetty.model

// 已兑换奖品（包含原商品信息+状态+兑换时间）
data class RedeemedPrize(
    val id: String, // 唯一标识
    val productName: String, // 商品名称
    val productPrice: Int, // 兑换时的价格
    var status: PrizeStatus, // 状态（可修改）
    val redeemTime: String // 兑换时间
)