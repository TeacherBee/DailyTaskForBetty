package com.example.dailytaskforbetty.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytaskforbetty.model.Product
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

    // 已兑换奖品列表
    private val _redeemedProducts = MutableStateFlow<List<Product>>(emptyList())
    val redeemedProducts: StateFlow<List<Product>> = _redeemedProducts.asStateFlow()

    // 兑换商品：需要传入TaskViewModel（用于修改总奖励）
    fun redeemProduct(productId: String, taskViewModel: TaskViewModel) {
        val currentReward = taskViewModel.totalReward.value
        viewModelScope.launch {
            _products.value = _products.value.map { product ->
                if (product.id == productId) {
                    if (product.stock > 0 && currentReward >= product.price) {
                        val newStock = product.stock - 1
                        // 调用TaskViewModel减少积分，并传入商品名称（用于记录历史）
                        taskViewModel.reduceReward(product.price, product.name)
                        // 记录已兑换奖品
                        _redeemedProducts.value = _redeemedProducts.value + product.copy(stock = 1) // 标记为已兑换
                        product.copy(stock = newStock)
                    } else {
                        product
                    }
                } else {
                    product
                }
            }
        }
    }
}