// filePath: dailytaskforbetty/model/ProductEntity.kt
package com.example.dailytaskforbetty.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val price: Int,
    var stock: Int
)

// 转换方法
fun ProductEntity.toProduct(): Product {
    return Product(
        id = id,
        name = name,
        price = price,
        stock = stock
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        price = price,
        stock = stock
    )
}