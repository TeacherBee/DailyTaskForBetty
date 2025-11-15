package com.example.dailytaskforbetty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ShopViewModel : ViewModel() {
    // 商品列表（预先设定初始商品）
    private val _products = MutableStateFlow<List<Product>>(
        listOf(
            Product(
                id = UUID.randomUUID().toString(),
                name = "笔记本",
                price = 5,
                stock = 3
            ),
            Product(
                id = UUID.randomUUID().toString(),
                name = "钢笔",
                price = 8,
                stock = 2
            ),
            Product(
                id = UUID.randomUUID().toString(),
                name = "书签",
                price = 3,
                stock = 5
            )
        )
    )
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    // 兑换商品：需要传入TaskViewModel（用于修改总奖励）
    fun redeemProduct(productId: String, taskViewModel: TaskViewModel) {
        val currentReward = taskViewModel.totalReward.value
        viewModelScope.launch {
            _products.value = _products.value.map { product ->
                if (product.id == productId) {
                    // 检查条件：库存>0 且 奖励>=价格
                    if (product.stock > 0 && currentReward >= product.price) {
                        // 1. 库存减1
                        val newStock = product.stock - 1
                        // 2. 总奖励减价格
                        taskViewModel.reduceReward(product.price)
                        product.copy(stock = newStock)
                    } else {
                        product // 不满足条件则不修改
                    }
                } else {
                    product
                }
            }
        }
    }
}