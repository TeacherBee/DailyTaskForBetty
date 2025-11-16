package com.example.dailytaskforbetty.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytaskforbetty.model.PrizeStatus
import com.example.dailytaskforbetty.model.Product
import com.example.dailytaskforbetty.model.RedeemedPrize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.UUID

class ShopViewModel : ViewModel() {
    // 商店商品列表（不变）
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

    // 替换原有_redeemedProducts：存储已兑换奖品（带状态）
    private val _redeemedPrizes = MutableStateFlow<List<RedeemedPrize>>(emptyList())
    val redeemedPrizes: StateFlow<List<RedeemedPrize>> = _redeemedPrizes.asStateFlow()

    // 兑换商品时：创建带状态的RedeemedPrize（初始状态为“待发货”）
    fun redeemProduct(productId: String, taskViewModel: TaskViewModel) {
        val currentReward = taskViewModel.totalReward.value
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).apply {
            timeZone = TimeZone.getTimeZone("Asia/Shanghai") // 北京时间
        }
        val redeemTime = sdf.format(Date())

        viewModelScope.launch {
            _products.value = _products.value.map { product ->
                if (product.id == productId && product.stock > 0 && currentReward >= product.price) {
                    // 1. 库存减1
                    val newStock = product.stock - 1
                    // 2. 扣积分（传入商品名用于记录历史）
                    taskViewModel.reduceReward(product.price, product.name)
                    // 3. 添加到已兑换奖品列表（初始状态：待发货）
                    _redeemedPrizes.value = _redeemedPrizes.value + RedeemedPrize(
                        id = UUID.randomUUID().toString(),
                        productName = product.name,
                        productPrice = product.price,
                        status = PrizeStatus.PENDING_SHIPMENT,
                        redeemTime = redeemTime
                    )
                    product.copy(stock = newStock)
                } else {
                    product
                }
            }
        }
    }

    // 确认收货（将状态改为“已收货”）
    fun confirmReceived(prizeId: String) {
        viewModelScope.launch {
            _redeemedPrizes.value = _redeemedPrizes.value.map { prize ->
                if (prize.id == prizeId) {
                    prize.copy(status = PrizeStatus.RECEIVED)
                } else {
                    prize
                }
            }
        }
    }
}