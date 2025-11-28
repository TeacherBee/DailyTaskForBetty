package com.example.dailytaskforbetty.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.foundation.clickable         // clickable
import androidx.compose.material3.MaterialTheme     // MaterialTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.CardDefaults
import androidx.navigation.compose.rememberNavController
import com.example.dailytaskforbetty.ui.theme.DailyTaskForBettyTheme
import androidx.compose.material3.Divider
import androidx.compose.ui.graphics.Color
import com.example.dailytaskforbetty.model.RewardHistory
import com.example.dailytaskforbetty.viewmodel.ShopViewModel
import com.example.dailytaskforbetty.viewmodel.TaskViewModel
import com.example.dailytaskforbetty.model.*
import com.example.dailytaskforbetty.data.*
import java.text.*
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


// 账户中心详情页
@Composable
fun AccountScreen(
    navController: NavController,
    taskViewModel: TaskViewModel = viewModel(),
    shopViewModel: ShopViewModel = viewModel(),
    redPacketDao: RedPacketDao // 新增参数
) {
    val totalReward by taskViewModel.totalReward.collectAsState()
    val rewardHistories by taskViewModel.rewardHistories.collectAsState(initial = emptyList())
    val redPacketBalance by redPacketDao.getRedPacketBalanceFlow().collectAsState(initial = null)
    val redPacketHistories by redPacketDao.getRedPacketHistories().collectAsState(initial = emptyList())

    // 状态管理
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var expandBalanceHistory by remember { mutableStateOf(false) }
    var expandPointHistory by remember { mutableStateOf(false) }

    // 格式化金额显示
    val decimalFormat = remember { DecimalFormat("0.00") }
    val currentBalance = redPacketBalance?.balance ?: 0.0

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

        // 红包余额卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "账户资产", style = MaterialTheme.typography.titleMedium)
                // 红包余额
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "红包余额",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${decimalFormat.format(currentBalance)} 元",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // 积分余额（新增部分）
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "积分余额",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "$totalReward 积分",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xE6FF5722) // 可以使用与积分相关的主题色
                    )
                }

                // 提现按钮（保持不变）
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Button(
                        onClick = { showWithdrawDialog = true },
                        enabled = currentBalance > 0
                    ) {
                        Text("提现")
                    }
                }
            }
        }

        // 余额历史
        MyExpandableItem(
            title = "余额历史",
            expanded = expandBalanceHistory,
            onTitleClick = { expandBalanceHistory = !expandBalanceHistory }
        ) {
            if (redPacketHistories.isEmpty()) {
                Text(
                    text = "暂无余额记录",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(redPacketHistories) { history ->
                        HistoryItem(
                            type = history.type,
                            amount = "${decimalFormat.format(history.amount)} 元",
                            reason = history.reason,
                            time = history.time,
                            isRedPacket = true
                        )
                    }
                }
            }
        }

        // 积分历史
        MyExpandableItem(
            title = "积分历史",
            expanded = expandPointHistory,
            onTitleClick = { expandPointHistory = !expandPointHistory }
        ) {
            if (rewardHistories.isEmpty()) {
                Text(
                    text = "暂无积分记录",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(rewardHistories) { history ->
                        HistoryItem(
                            type = history.type,
                            amount = "${history.amount} 积分",
                            reason = history.reason,
                            time = history.time,
                            isRedPacket = false
                        )
                    }
                }
            }
        }
    }

    // 提现弹窗
    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = { Text("申请提现") },
            text = {
                Text("本次提现金额${decimalFormat.format(currentBalance)}元，请截图给管理员哦~")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showWithdrawDialog = false
                        // 调用ViewModel的提现处理函数（避免在Composable中直接用viewModelScope）
                        shopViewModel.handleWithdraw(currentBalance)
                    }
                ) {
                    Text("确认")
                }
            },
            dismissButton = {
                Button(onClick = { showWithdrawDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

// 可展开的条目组件
@Composable
private fun MyExpandableItem(
    title: String,
    expanded: Boolean,
    onTitleClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // 标题行
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTitleClick() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
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

            // 展开内容
            if (expanded) {
                Divider()
                content()
            }
        }
    }
}

// 历史记录项组件
@Composable
private fun HistoryItem(
    type: String,
    amount: String,
    reason: String,
    time: String,
    isRedPacket: Boolean
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = reason,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = amount,
                color = if (type == "获得" || type == "收入")
                    Color(0xFF4CAF50)
                else
                    Color(0xFFF44336),
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = type,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}