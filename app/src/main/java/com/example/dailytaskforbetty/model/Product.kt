package com.example.dailytaskforbetty.model

// 商品数据类：ID、名称、价格（需要的积分）、库存
data class Product(
    val id: String,
    val name: String,
    val price: Int,
    var stock: Int // 库存（var修饰，允许修改）
)