package com.example.dailytaskforbetty.viewmodel

import com.example.dailytaskforbetty.data.*
import com.example.dailytaskforbetty.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Date
import java.text.SimpleDateFormat
import java.util.*

class MockRewardDao : RewardDao {
    // 模拟总积分Flow（返回100作为示例）
    override fun getTotalRewardFlow(): Flow<TotalReward?> {
        return flowOf(TotalReward(amount = 100))
    }

    // 模拟插入或替换总积分（空实现）
    override suspend fun insertOrReplaceTotalReward(totalReward: TotalReward) {}

    // 模拟积分历史Flow（返回假数据）
    override fun getRewardHistories(): Flow<List<RewardHistory>> {
        return flowOf(emptyList()) // 预览时默认空列表
    }

    // 模拟插入积分历史（空实现）
    override suspend fun insertRewardHistory(history: RewardHistory) {}
}

class MockTaskDao : TaskDao {
    // 模拟返回预设任务
    override fun observeAllTasks(): Flow<List<TaskEntity>> {
        val mockTime = System.currentTimeMillis()
        val mockEntities = listOf(
            Task(
                id = "mock1",
                title = "喝水",
                isCompleted = false,
                reward = 5,
                cycle = TaskCycle.DAILY,
                lastCompletedTime = null,
                nextRefreshTime = Date(mockTime + 86400000) // 24小时后刷新
            ).toEntity(),
            Task(
                id = "mock2",
                title = "运动",
                isCompleted = false,
                reward = 15,
                cycle = TaskCycle.WEEKLY,
                lastCompletedTime = null,
                nextRefreshTime = Date(mockTime + 604800000) // 7天后刷新
            ).toEntity()
        )
        return flowOf(mockEntities)
    }

    // 其他方法模拟实现（空实现即可，预览不依赖实际存储）
    override suspend fun upsertTask(task: TaskEntity) {}
    override suspend fun upsertTasks(tasks: List<TaskEntity>) {}
    override suspend fun deleteTask(task: TaskEntity) {}
    override suspend fun clearAllTasks() {}
}

class MockRedeemedPrizeDao : RedeemedPrizeDao {
    // 模拟已兑换奖品数据
    override fun observeAllRedeemedPrizes(): Flow<List<RedeemedPrizeEntity>> {
        // 生成模拟时间（当前时间格式化）
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
        val mockTime = sdf.format(Date())

        // 模拟已兑换奖品列表
        val mockEntities = listOf(
            RedeemedPrizeEntity(
                id = UUID.randomUUID().toString(),
                productName = "笔记本",
                productPrice = 5,
                status = PrizeStatus.PENDING_SHIPMENT.name,
                redeemTime = mockTime
            ),
            RedeemedPrizeEntity(
                id = UUID.randomUUID().toString(),
                productName = "钢笔",
                productPrice = 8,
                status = PrizeStatus.RECEIVED.name,
                redeemTime = sdf.format(Date(System.currentTimeMillis() - 86400000)) // 昨天的时间
            )
        )
        return flowOf(mockEntities)
    }

    // 模拟插入已兑换奖品（空实现，预览无需实际存储）
    override suspend fun insertRedeemedPrize(entity: RedeemedPrizeEntity) {}

    // 模拟更新已兑换奖品（空实现，预览无需实际存储）
    override suspend fun updateRedeemedPrize(entity: RedeemedPrizeEntity) {}

    // 新增：实现获取单个奖品的模拟方法
    override suspend fun getRedeemedPrizeById(id: String): RedeemedPrizeEntity? {
        // 模拟查询，返回null或找到的模拟实体（这里简单返回null）
        return null
    }
}

class MockProductDao : ProductDao {
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    private val mockTime = sdf.format(Date())
    private val mockProducts = listOf(
        // 笔记本：每周刷新
        Product(
            id = "mock_product1",
            name = "笔记本",
            price = 5,
            stock = 3,
            refreshCycle = StockRefreshCycle.WEEKLY,
            lastRefreshTime = mockTime,
            initialStock = 3
        ).toEntity(),
        // 钢笔：每日刷新
        Product(
            id = "mock_product2",
            name = "钢笔",
            price = 8,
            stock = 2,
            refreshCycle = StockRefreshCycle.DAILY,
            lastRefreshTime = mockTime,
            initialStock = 2
        ).toEntity(),
        // 书签：不刷新
        Product(
            id = "mock_product3",
            name = "书签",
            price = 3,
            stock = 5,
            refreshCycle = StockRefreshCycle.NONE,
            lastRefreshTime = mockTime,
            initialStock = 5
        ).toEntity()
    )

    // 模拟观察商品列表（返回预设数据）
    override fun observeAllProducts(): Flow<List<ProductEntity>> {
        return flowOf(mockProducts)
    }

    // 模拟更新商品（空实现，预览不实际修改）
    override suspend fun updateProduct(product: ProductEntity) {}

    // 模拟根据ID获取商品（从预设列表中查找）
    override suspend fun getProductById(id: String): ProductEntity? {
        return mockProducts.find { it.id == id }
    }

    // 模拟清空商品（空实现）
    override suspend fun clearAllProducts() {}

    // 模拟批量插入商品（空实现）
    override suspend fun insertProducts(products: List<ProductEntity>) {}
}

class MockRedPacketDao : RedPacketDao {
    // 模拟红包余额
    override fun getRedPacketBalanceFlow(): Flow<RedPacketBalanceEntity?> {
        return flowOf(RedPacketBalanceEntity(balance = 68.52))
    }

    // 模拟插入或替换红包余额
    override suspend fun insertOrReplaceRedPacketBalance(balance: RedPacketBalanceEntity) {}

    // 模拟红包历史（修正时区）
    override fun getRedPacketHistories(): Flow<List<RedPacketHistoryEntity>> {
        // 关键：设置时区为北京时间
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).apply {
            timeZone = TimeZone.getTimeZone("Asia/Shanghai")
        }
        val mockTime = sdf.format(Date())
        val yesterday = sdf.format(Date(System.currentTimeMillis() - 86400000)) // 昨天的时间（正确时区）

        return flowOf(
            listOf(
                RedPacketHistoryEntity(
                    type = "收入",
                    amount = 0.52,
                    reason = "兑换：每日暖心小小红包~",
                    time = yesterday
                ),
                RedPacketHistoryEntity(
                    type = "收入",
                    amount = 18.88,
                    reason = "兑换：随机小红包！",
                    time = mockTime
                )
            )
        )
    }

    // 模拟插入红包历史
    override suspend fun insertRedPacketHistory(history: RedPacketHistoryEntity) {}
}