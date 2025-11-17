package com.example.dailytaskforbetty.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.filled.AccountBalance // 账户余额图标
import com.example.dailytaskforbetty.viewmodel.*
import com.example.dailytaskforbetty.navigation.NavRoutes
import com.example.dailytaskforbetty.ui.theme.DailyTaskForBettyTheme



// “我的”主界面：包含各个栏目入口
@Composable
fun MyScreen(
    navController: NavController, // 用于导航到详情页
    taskViewModel: TaskViewModel,
    userViewModel: UserViewModel
) {
    val totalReward by taskViewModel.totalReward.collectAsState()
    val userInfo by userViewModel.userInfo.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        // 顶部个人信息卡片（显示头像、姓名、总积分）
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 头像+姓名
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "头像",
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = userInfo.name,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    // 总积分
                    Text(
                        text = "总积分：$totalReward",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // 栏目列表：个人信息、账户、其他
        items(4) { index ->
            when (index) {
                0 -> MyScreenItem(
                    icon = Icons.Default.AccountCircle,
                    title = "个人信息",
                    description = "姓名、生日等信息"
                ) {
                    // 点击跳转到个人信息详情页
                    navController.navigate(NavRoutes.USER_INFO_SCREEN)
                }
                1 -> MyScreenItem(
                    icon = Icons.Default.AccountBalance,
                    title = "账户中心",
                    description = "积分、历史、已兑换奖品"
                ) {
                    // 点击跳转到账户详情页
                    navController.navigate(NavRoutes.ACCOUNT_SCREEN)
                }
                2 -> MyScreenItem( // 新增：我的奖品
                    icon = Icons.Default.CardGiftcard,
                    title = "我的奖品",
                    description = "已兑换奖品及物流状态"
                ) {
                    navController.navigate(NavRoutes.MY_PRIZES_SCREEN)
                }
                3 -> MyScreenItem(
                    icon = Icons.Default.Settings,
                    title = "设置",
                    description = "其他功能设置"
                ) {
                    // 点击跳转到设置页（示例，可扩展）
                    navController.navigate(NavRoutes.SETTINGS_SCREEN)
                }
            }
        }
    }
}

// “我的”界面的单个栏目项
@Composable
private fun MyScreenItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onClick() }, // 点击事件
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // 右箭头（提示可点击）
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "进入详情"
            )
        }
    }
}

// 预览
@Preview(showBackground = true)
@Composable
fun MyScreenPreview() {
    DailyTaskForBettyTheme {
        // 1. 创建模拟的Dao
        val mockRewardDao = MockRewardDao()
        // 2. 用模拟Dao创建TaskViewModel
        val previewTaskViewModel = TaskViewModel(rewardDao = mockRewardDao)
        // 3. 如果UserViewModel有构造参数，也用模拟数据创建（这里假设它无参）
        val previewUserViewModel = UserViewModel()

        // 4. 传入预览ViewModel
        MyScreen(
            navController = rememberNavController(),
            taskViewModel = previewTaskViewModel, // 使用带模拟Dao的ViewModel
            userViewModel = previewUserViewModel
        )
    }
}