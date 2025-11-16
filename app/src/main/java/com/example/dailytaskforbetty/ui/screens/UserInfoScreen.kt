package com.example.dailytaskforbetty.ui.screens

import androidx.compose.foundation.layout.*
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
import com.example.dailytaskforbetty.viewmodel.UserViewModel

import com.example.dailytaskforbetty.ui.theme.DailyTaskForBettyTheme

// 个人信息详情页
@Composable
fun UserInfoScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    val userInfo by userViewModel.userInfo.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 返回按钮（简化版，实际可添加IconButton）
        Text(
            text = "← 返回",
            modifier = Modifier
                .padding(bottom = 16.dp)
                .clickable { navController.popBackStack() },
            color = MaterialTheme.colorScheme.primary
        )

        // 个人信息卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "个人信息",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // 信息列表
                UserInfoItem(label = "姓名", value = userInfo.name)
                UserInfoItem(label = "生日", value = userInfo.birthday)
                UserInfoItem(label = "性别", value = userInfo.gender)
                UserInfoItem(label = "个性签名", value = userInfo.signature)
            }
        }
    }
}

// 个人信息项（标签+值）
@Composable
private fun UserInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label：",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// 预览
@Preview(showBackground = true)
@Composable
fun UserInfoScreenPreview() {
    DailyTaskForBettyTheme {
        UserInfoScreen(navController = rememberNavController())
    }
}