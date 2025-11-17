package com.example.dailytaskforbetty.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytaskforbetty.model.*
import com.example.dailytaskforbetty.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.UUID
import kotlinx.coroutines.flow.Flow               // Flow 本身
import kotlinx.coroutines.flow.map                // map 扩展
import kotlinx.coroutines.flow.first              // first 扩展

class ShopViewModel(
    private val redeemedPrizeDao: RedeemedPrizeDao
) : ViewModel() {
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

    // 从数据库获取已兑换奖品（替代原有的内存列表）
    val redeemedPrizes: Flow<List<RedeemedPrize>> = redeemedPrizeDao.observeAllRedeemedPrizes()
        .map { entities -> entities.map { it.toRedeemedPrize() } }

    // 兑换商品时：保存到数据库
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
                    // 3. 创建已兑换奖品并保存到数据库
                    val newPrize = RedeemedPrize(
                        id = UUID.randomUUID().toString(),
                        productName = product.name,
                        productPrice = product.price,
                        status = PrizeStatus.PENDING_SHIPMENT,
                        redeemTime = redeemTime
                    )
                    redeemedPrizeDao.insertRedeemedPrize(newPrize.toEntity())

                    product.copy(stock = newStock)
                } else {
                    product
                }
            }
        }
    }

    // 确认收货（更新数据库中的状态）
    fun confirmReceived(prizeId: String) {
        viewModelScope.launch {
            // 查询对应奖品并更新状态
            val entities = redeemedPrizeDao.observeAllRedeemedPrizes().first()
            entities.find { it.id == prizeId }?.let { entity ->
                val updatedEntity = entity.copy(status = PrizeStatus.RECEIVED.name)
                redeemedPrizeDao.updateRedeemedPrize(updatedEntity)
            }
        }
    }
}