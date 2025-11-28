package com.example.dailytaskforbetty.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dailytaskforbetty.model.PrizeStatus
import com.example.dailytaskforbetty.model.RedeemedPrize
import com.example.dailytaskforbetty.viewmodel.ShopViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable         // clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.MaterialTheme     // MaterialTheme
import androidx.navigation.compose.rememberNavController

// “我的奖品”页面：展示所有已兑换奖品及状态
@Composable
fun MyPrizesScreen(
    navController: NavController,
    shopViewModel: ShopViewModel = viewModel()
) {
    val redeemedPrizes by shopViewModel.redeemedPrizes.collectAsState(initial = emptyList())

    // 过滤掉红包类奖品
    val filteredPrizes = redeemedPrizes.filter { prize ->
        !listOf(
            "每日暖心小小红包~",
            "随机小红包！",
            "随机中红包！！",
            "随机大红包！！！"
        ).contains(prize.productName)
    }

    // 区分已收货和未收货
    val receivedPrizes = filteredPrizes.filter { it.status == PrizeStatus.RECEIVED }
    val pendingPrizes = filteredPrizes.filter { it.status != PrizeStatus.RECEIVED }

    // 展开状态管理
    var showPending by remember { mutableStateOf(true) }
    var showReceived by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 返回按钮
        Text(
            text = "← 返回",
            modifier = Modifier
                .padding(bottom = 16.dp)
                .clickable { navController.popBackStack() },
            color = MaterialTheme.colorScheme.primary
        )

        // 页面标题
        Text(
            text = "我的奖品",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 奖品列表
        if (redeemedPrizes.isEmpty()) {
            // 无奖品时的提示
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无已兑换的奖品",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // 未收货区域
            CategorySection(
                title = "未收货 (${pendingPrizes.size})",
                expanded = showPending,
                onToggle = { showPending = !showPending }
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(pendingPrizes) { prize ->
                        PrizeItem(
                            prize = prize,
                            onConfirmReceived = { shopViewModel.confirmReceived(prize.id) }
                        )
                    }
                }
            }

            // 已收货区域
            CategorySection(
                title = "已收货 (${receivedPrizes.size})",
                expanded = showReceived,
                onToggle = { showReceived = !showReceived }
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(receivedPrizes) { prize ->
                        PrizeItem(
                            prize = prize,
                            onConfirmReceived = { shopViewModel.confirmReceived(prize.id) }
                        )
                    }
                }
            }
        }
    }
}

// 分类区域组件
@Composable
private fun CategorySection(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = if (expanded)
                    Icons.Default.KeyboardArrowUp
                else
                    Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "收起" else "展开"
            )
        }
        if (expanded) {
            content()
        }
    }
}

// 单个奖品项UI（显示状态和操作按钮）
@Composable
private fun PrizeItem(
    prize: RedeemedPrize,
    onConfirmReceived: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 商品名称和价格
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = prize.productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${prize.productPrice} 积分",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 兑换时间
            Text(
                text = "兑换时间：${prize.redeemTime}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 状态标签和操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 状态标签（不同状态显示不同颜色）
                val (statusText, statusColor) = when (prize.status) {
                    PrizeStatus.PENDING_SHIPMENT -> "待发货" to androidx.compose.ui.graphics.Color(0xFFFF9800) // 橙色
                    PrizeStatus.SHIPPED -> "已发货" to androidx.compose.ui.graphics.Color(0xFF2196F3) // 蓝色
                    PrizeStatus.RECEIVED -> "已收货" to androidx.compose.ui.graphics.Color(0xFF4CAF50) // 绿色
                }
                Text(
                    text = "状态：$statusText",
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold
                )

                // 确认收货按钮（仅待发货/已发货状态显示）
                if (prize.status != PrizeStatus.RECEIVED) {
                    Button(
                        onClick = onConfirmReceived,
                        modifier = Modifier.size(width = 120.dp, height = 36.dp)
                    ) {
                        Text("确认收货")
                    }
                }
            }
        }
    }
}

// 预览
@Preview(showBackground = true)
@Composable
fun MyPrizesScreenPreview() {
    MaterialTheme {
        MyPrizesScreen(navController = rememberNavController())
    }
}