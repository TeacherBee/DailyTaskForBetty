package com.example.dailytaskforbetty

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect

// “我的”页面：展示累计奖励
@Composable
fun MyScreen(
    taskViewModel: TaskViewModel // 复用TaskViewModel获取总奖励
) {
    val totalReward by taskViewModel.totalReward.collectAsState()

    // 调试用：打印当前总奖励（可选）
    LaunchedEffect(Unit) {
        taskViewModel.totalReward.collect {
            println("我的页面总奖励更新：$it")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "我的奖励",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Text(
            text = "累计获得：$totalReward 积分",
            fontSize = 24.sp,
            color = androidx.compose.ui.graphics.Color(0xFF6200EE) // 主题色
        )
    }
}