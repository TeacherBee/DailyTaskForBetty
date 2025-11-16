package com.example.dailytaskforbetty.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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

// 账户中心详情页
@Composable
fun AccountScreen(
    navController: NavController,
    taskViewModel: TaskViewModel = viewModel(),
    shopViewModel: ShopViewModel = viewModel()
) {
    val totalReward by taskViewModel.totalReward.collectAsState()
    val rewardHistories by taskViewModel.rewardHistories.collectAsState()
    val redeemedPrizes by shopViewModel.redeemedPrizes.collectAsState()

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

        // 剩余积分
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "剩余积分", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = totalReward.toString(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 积分历史
        Text(
            text = "积分历史",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            if (rewardHistories.isEmpty()) {
                Text(
                    text = "暂无积分记录",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(rewardHistories.reversed()) { history -> // 倒序显示（最新的在前）
                        HistoryItem(history = history)
                    }
                }
            }
        }
    }
}

// 积分历史项
@Composable
private fun HistoryItem(history: RewardHistory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = history.reason, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = history.time,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = if (history.type == "获得") "+${history.amount}" else "-${history.amount}",
            color = if (history.type == "获得") Color(0xFF00C853) else Color(0xFFD32F2F)
        )
    }
    Divider() // 分割线
}

// 预览
@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    DailyTaskForBettyTheme {
        AccountScreen(navController = rememberNavController())
    }
}