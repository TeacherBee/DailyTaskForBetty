package com.example.dailytaskforbetty.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val price: Int,
    var stock: Int,
    val refreshCycle: String, // 存储枚举的字符串形式（DAILY/WEEKLY/NONE）
    val lastRefreshTime: String,
    val initialStock: Int
)

// 转换方法更新
fun ProductEntity.toProduct(): Product {
    return Product(
        id = id,
        name = name,
        price = price,
        stock = stock,
        refreshCycle = StockRefreshCycle.valueOf(refreshCycle), // 字符串转枚举
        lastRefreshTime = lastRefreshTime,
        initialStock = initialStock
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        price = price,
        stock = stock,
        refreshCycle = refreshCycle.name, // 枚举转字符串存储
        lastRefreshTime = lastRefreshTime,
        initialStock = initialStock
    )
}