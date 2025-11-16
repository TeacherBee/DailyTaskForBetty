package com.example.dailytaskforbetty

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.clickable         // clickable
import androidx.compose.material3.MaterialTheme     // MaterialTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.CardDefaults
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import com.example.dailytaskforbetty.ui.theme.DailyTaskForBettyTheme

// 设置页面（示例，可扩展功能）
@Composable
fun SettingsScreen(navController: NavController) {
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

        Text(
            text = "设置",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "功能开发中...",
            modifier = Modifier.align(androidx.compose.ui.Alignment.Companion.CenterHorizontally)
        )
    }
}

// 预览
@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    DailyTaskForBettyTheme {
        SettingsScreen(navController = rememberNavController())
    }
}