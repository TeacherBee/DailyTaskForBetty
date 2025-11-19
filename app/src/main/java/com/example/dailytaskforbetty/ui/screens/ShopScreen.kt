package com.example.dailytaskforbetty.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.*
import com.example.dailytaskforbetty.model.Product
import com.example.dailytaskforbetty.model.StockRefreshCycle
import com.example.dailytaskforbetty.viewmodel.*
import com.example.dailytaskforbetty.ui.theme.DailyTaskForBettyTheme

// 商店页面：展示商品和兑换功能
@Composable
fun ShopScreen(
    shopViewModel: ShopViewModel = viewModel(),
    taskViewModel: TaskViewModel // 从导航传入，共享总奖励
) {
    val products by shopViewModel.products.collectAsState()
    val totalReward by taskViewModel.totalReward.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 页面标题 + 当前可兑换积分
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "积分商店",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "当前积分：$totalReward",
                color = Color(0xFF6200EE)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 商品列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                ProductItem(
                    product = product,
                    totalReward = totalReward,
                    onRedeem = {
                        // 调用兑换函数，传入TaskViewModel
                        shopViewModel.redeemProduct(product.id, taskViewModel)
                    }
                )
            }
        }
    }
}

// 单个商品项UI
@Composable
private fun ProductItem(
    product: Product,
    totalReward: Int, // 当前总积分（用于判断是否可兑换）
    onRedeem: () -> Unit
) {
    // 控制弹窗显示的状态
    var showInsufficientDialog by remember { mutableStateOf(false) }

    // 只要库存>0就允许点击（不管积分是否足够）
    // val isRedeemable = product.stock > 0 && totalReward >= product.price
    val isButtonEnabled = product.stock > 0

    // 点击兑换按钮的逻辑（先判断积分是否足够）
    val handleRedeem = {
        if (totalReward >= product.price) {
            onRedeem() // 积分足够，执行兑换
        } else {
            showInsufficientDialog = true // 积分不足，显示弹窗
        }
    }

    // 计算下次刷新时间
    val nextRefreshTime = calculateNextRefreshTime(product)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 商品信息（不变）
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = "价格：${product.price} 积分", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "库存：${product.stock}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (product.stock > 0) Color.Green else Color.Red
                )
            }

            // 显示下次刷新时间
            if (product.refreshCycle != StockRefreshCycle.NONE) {
                Text(
                    text = "下次刷新：$nextRefreshTime",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2196F3) // 蓝色突出显示
                )
            }
            else {
                Text(
                    text = "下次刷新：稍等一下哦~",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2196F3) // 蓝色突出显示
                )
            }

            // 兑换按钮（放大图标）
            Button(
                onClick = handleRedeem,
                enabled = isButtonEnabled,
                modifier = Modifier.size(64.dp), // 按钮尺寸放大（原56dp）
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp) // 去除内边距，让图标更大
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "兑换商品",
                    modifier = Modifier.size(32.dp) // 图标放大（原默认尺寸约24dp）
                )
            }
        }
    }

    // 积分不足时的弹窗
    if (showInsufficientDialog) {
        AlertDialog(
            onDismissRequest = { showInsufficientDialog = false }, // 点击外部关闭
            title = { Text("兑换失败") },
            text = { Text("您的积分不足，当前积分：$totalReward，所需积分：${product.price}") },
            confirmButton = {
                Button(onClick = { showInsufficientDialog = false }) {
                    Text("确定")
                }
            }
        )
    }
}

// 计算下次刷新时间的工具函数
private fun calculateNextRefreshTime(product: Product): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).apply {
        timeZone = TimeZone.getTimeZone("Asia/Shanghai")
    }
    val lastRefresh = sdf.parse(product.lastRefreshTime) ?: Date()
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai")).apply {
        time = lastRefresh
    }

    return when (product.refreshCycle) {
        StockRefreshCycle.DAILY -> {
            // 下次刷新为次日0点
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            sdf.format(calendar.time)
        }
        StockRefreshCycle.THREE_DAYS -> {
            // 下次刷新为三日后的0点
            calendar.add(Calendar.DAY_OF_YEAR, 3)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            sdf.format(calendar.time)
        }
        StockRefreshCycle.WEEKLY -> {
            // 下次刷新为下周同一日的0点
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            sdf.format(calendar.time)
        }
        else -> "不自动刷新"
    }
}

@Preview(showBackground = true)
@Composable
fun ShopScreenPreview() {
    // 1. 创建模拟的Dao
    val mockRewardDao = MockRewardDao()
    val mockTaskDao = MockTaskDao()
    val mockRedeemedPrizeDao = MockRedeemedPrizeDao()
    val mockProductDao = MockProductDao()
    // 2. 用模拟Dao创建TaskViewModel
    val previewTaskViewModel = TaskViewModel(
        rewardDao = mockRewardDao,
        taskDao = mockTaskDao
    )
    val previewShopViewModel = ShopViewModel(
        redeemedPrizeDao = mockRedeemedPrizeDao,
        productDao = mockProductDao

    )

    DailyTaskForBettyTheme {
        ShopScreen(
            shopViewModel = previewShopViewModel,
            taskViewModel = previewTaskViewModel
        )
    }
}