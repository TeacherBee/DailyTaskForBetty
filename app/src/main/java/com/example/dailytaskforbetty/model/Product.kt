package com.example.dailytaskforbetty.model

// 库存刷新周期枚举
enum class StockRefreshCycle {
    DAILY,   // 每日刷新
    THREE_DAYS, // 三天
    WEEKLY,  // 每周刷新
    NONE     // 不自动刷新（默认）
}

// 商品数据类：ID、名称、价格（需要的积分）、库存
data class Product(
    val id: String,
    val name: String,
    val price: Int,
    var stock: Int, // 库存（var修饰，允许修改）
    val refreshCycle: StockRefreshCycle, // 刷新周期（每日/每周）
    val lastRefreshTime: String,         // 上次刷新库存的时间
    val initialStock: Int                // 初始库存（刷新时恢复到此值）
)