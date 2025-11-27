package com.example.dailytaskforbetty.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytaskforbetty.model.*
import com.example.dailytaskforbetty.data.*
import com.example.dailytaskforbetty.utils.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.threeten.bp.format.DateTimeFormatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.UUID
import kotlinx.coroutines.flow.Flow               // Flow 本身
import kotlinx.coroutines.flow.map                // map 扩展
import kotlinx.coroutines.flow.first              // first 扩展
import java.util.*
import kotlinx.coroutines.delay
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit
import android.util.Log
import kotlin.random.Random

class ShopViewModel(
    private val redeemedPrizeDao: RedeemedPrizeDao,
    private val productDao: ProductDao,
    val redPacketDao: RedPacketDao
) : ViewModel() {
    // 从数据库获取商品列表
    // 1. 只保留 Flow
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    // 初始化商品数据（首次运行时）
    init {
        viewModelScope.launch {
//            Log.d("ShopViewModel", "开始初始化商品数据...")
            // 监听数据库商品变化，实时更新 _products
            viewModelScope.launch {
                productDao.observeAllProducts().collect { entities ->
                    _products.value = entities.map { it.toProduct() }
                    Log.d("ShopViewModel", "数据库商品变化，当前商品数：${entities.size}")
                }
            }

            val existingProducts = productDao.observeAllProducts().first()
//            Log.d("ShopViewModel", "现有商品数量：${existingProducts.size}") // 关键日志
//            Log.d("ShopViewModel", "是否需要插入默认商品：${existingProducts.isEmpty()}")
            if (existingProducts.isEmpty()) {
                // 初始化时间对齐到当天0点
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai")).apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val initTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(calendar.time)

                // 插入默认商品（配置刷新规则）
                val defaultProducts = listOf(
                    Product(
                        id = UUID.randomUUID().toString(),
                        name = "每日暖心小小红包~",
                        price = 0,
                        stock = 1,
                        refreshCycle = StockRefreshCycle.DAILY,  // 每日
                        lastRefreshTime = initTime,
                        initialStock = 1
                    ),
                    Product(
                        id = UUID.randomUUID().toString(),
                        name = "随机小红包！",
                        price = 10,
                        stock = 3,
                        refreshCycle = StockRefreshCycle.DAILY,  // 每日
                        lastRefreshTime = initTime,
                        initialStock = 3
                    ),
                    Product(
                        id = UUID.randomUUID().toString(),
                        name = "随机中红包！！",
                        price = 30,
                        stock = 1,
                        refreshCycle = StockRefreshCycle.THREE_DAYS, // 每三天
                        lastRefreshTime = initTime,
                        initialStock = 1
                    ),
                    Product(
                        id = UUID.randomUUID().toString(),
                        name = "随机大红包！！！",
                        price = 70,
                        stock = 1,
                        refreshCycle = StockRefreshCycle.WEEKLY, // 每周
                        lastRefreshTime = initTime,
                        initialStock = 1
                    ),
                    Product(
                        id = UUID.randomUUID().toString(),
                        name = "惊喜小礼盒OVO",
                        price = 15,
                        stock = 2,
                        refreshCycle = StockRefreshCycle.DAILY,
                        lastRefreshTime = initTime,
                        initialStock = 2
                    ),
                    Product(
                        id = UUID.randomUUID().toString(),
                        name = "可口外卖QAQ",
                        price = 20,
                        stock = 1,
                        refreshCycle = StockRefreshCycle.DAILY,
                        lastRefreshTime = initTime,
                        initialStock = 1
                    ),
                    Product(
                        id = UUID.randomUUID().toString(),
                        name = "劲爆大餐^_^ ",
                        price = 60,
                        stock = 1,
                        refreshCycle = StockRefreshCycle.THREE_DAYS,
                        lastRefreshTime = initTime,
                        initialStock = 1
                    ),
                    Product(
                        id = UUID.randomUUID().toString(),
                        name = "闪现",
                        price = 0,
                        stock = 1,
                        refreshCycle = StockRefreshCycle.NONE,
                        lastRefreshTime = initTime,
                        initialStock = 1
                    )
                ).map { it.toEntity() }

                productDao.insertProducts(defaultProducts)
            }
        }
        // 启动库存刷新检查（应用启动后开始）
        startStockRefreshChecker()
    }

    // 定时检查并刷新库存
    private fun startStockRefreshChecker() {
        viewModelScope.launch {
            while (true) {
                // 每1小时检查一次（可调整频率）
                delay(1800000) // 3600000毫秒 = 1小时
                checkAndRefreshStock()
            }
        }
    }

    // 检查所有商品是否需要刷新库存
    private suspend fun checkAndRefreshStock() {
        val zone = ZoneId.of("Asia/Shanghai")
        val currentTime = LocalDateTime.now(zone)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(zone)

        val entities = productDao.observeAllProducts().first()
        entities.forEach { entity ->
            val product = entity.toProduct()
            if (product.refreshCycle == StockRefreshCycle.NONE) return@forEach

            val lastRefresh = try {
                LocalDateTime.parse(product.lastRefreshTime, formatter)
            } catch (e: Exception) {
                currentTime.minusDays(1)          // 格式错误就当成昨天
            }

            val needRefresh = when (product.refreshCycle) {
                StockRefreshCycle.DAILY ->
                    currentTime.toLocalDate().isAfter(lastRefresh.toLocalDate())
                StockRefreshCycle.THREE_DAYS ->
                    ChronoUnit.DAYS.between(lastRefresh.toLocalDate(), currentTime.toLocalDate()) >= 3
                StockRefreshCycle.WEEKLY ->
                    ChronoUnit.WEEKS.between(lastRefresh.toLocalDate(), currentTime.toLocalDate()) >= 1
                StockRefreshCycle.NONE -> false
            }

            if (needRefresh) {
                val lastRefreshDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                    .parse(product.lastRefreshTime)!!
                val nextRefreshDate = TimeUtils.calculateNextRefreshTime(product.refreshCycle, lastRefreshDate)
                val refreshTimeStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                    .format(nextRefreshDate)

                productDao.updateProduct(
                    entity.copy(
                        stock = product.initialStock,
                        lastRefreshTime = refreshTimeStr
                    )
                )
            }
        }
    }

    // 从数据库获取已兑换奖品（替代原有的内存列表）
    val redeemedPrizes: Flow<List<RedeemedPrize>> = redeemedPrizeDao.observeAllRedeemedPrizes()
        .map { entities -> entities.map { it.toRedeemedPrize() } }

    // 兑换商品时：保存到数据库
    fun redeemProduct(productId: String, taskViewModel: TaskViewModel) {
        val currentReward = taskViewModel.totalReward.value
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).apply {
            timeZone = TimeZone.getTimeZone("Asia/Shanghai")
        }
        val redeemTime = sdf.format(Date())

        viewModelScope.launch {
            // 从数据库获取商品并更新库存
            val productEntity = productDao.getProductById(productId)
            if (productEntity != null && productEntity.stock > 0 && currentReward >= productEntity.price) {
                // 1. 库存减1
                val updatedProduct = productEntity.copy(stock = productEntity.stock - 1)
                productDao.updateProduct(updatedProduct)

                // 2. 扣积分
                taskViewModel.reduceReward(productEntity.price, productEntity.name)

                // 3. 创建已兑换奖品并保存到数据库
                val newPrize = RedeemedPrize(
                    id = UUID.randomUUID().toString(),
                    productName = productEntity.name,
                    productPrice = productEntity.price,
                    status = PrizeStatus.PENDING_SHIPMENT,
                    redeemTime = redeemTime
                )
                redeemedPrizeDao.insertRedeemedPrize(newPrize.toEntity())

                // 4. 处理红包兑现
                handleRedPacketRedeem(productEntity.name, redeemTime)
            }
        }
    }

    // 处理红包兑现
    private suspend fun handleRedPacketRedeem(productName: String, time: String) {
        val amount = when (productName) {
            "每日暖心小小红包~" -> 0.52 // 固定金额
            "随机小红包！"   -> Random.nextDouble(0.01, 18.88 + 0.01)   // 左闭右开
            "随机中红包！！"  -> Random.nextDouble(28.88, 58.88 + 0.01)
            "随机大红包！！！" -> Random.nextDouble(68.88, 138.88 + 0.01)
            else -> 0.0 // 非红包类奖品不处理
        }

        if (amount > 0) {
            // 保留两位小数
            val formattedAmount = String.format("%.2f", amount).toDouble()

            // 更新红包余额
            val currentBalance = redPacketDao.getRedPacketBalanceFlow().first()
            val newBalance = (currentBalance?.balance ?: 0.0) + formattedAmount
            redPacketDao.insertOrReplaceRedPacketBalance(
                RedPacketBalanceEntity(balance = newBalance)
            )

            // 记录红包历史
            redPacketDao.insertRedPacketHistory(
                RedPacketHistoryEntity(
                    type = "收入",
                    amount = formattedAmount,
                    reason = "兑换：$productName",
                    time = time
                )
            )
        }
    }

    fun handleWithdraw(amount: Double) {
        viewModelScope.launch {
            // 补全 RedPacketBalanceEntity 的 `initial` 参数（根据你的实体类定义调整）
            val balanceEntity = RedPacketBalanceEntity(
                id = 1, // 对应实体类的@PrimaryKey参数
                balance = 0.0
            )
            redPacketDao.insertOrReplaceRedPacketBalance(balanceEntity)

            // 插入提现历史
            val time = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(Date())
            redPacketDao.insertRedPacketHistory(
                RedPacketHistoryEntity(
                    type = "提现",
                    amount = amount,
                    reason = "手动提现",
                    time = time
                )
            )
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