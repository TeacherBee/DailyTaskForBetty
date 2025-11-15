package com.example.dailytaskforbetty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel // 补充viewModel导入
import com.example.dailytaskforbetty.ui.theme.DailyTaskForBettyTheme
import androidx.compose.ui.tooling.preview.Preview // 补充Preview导入

// 新增导航相关导入
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyTaskForBettyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskTimeApp()  // 入口：展示任务列表和添加区域
                }
            }
        }
    }
}

// 整个任务App的UI
@Composable
fun TaskApp(
    taskViewModel: TaskViewModel = viewModel() // 获取TaskViewModel实例
) {
    val tasks by taskViewModel.tasks.collectAsState() // 观察任务列表变化
    var newTaskTitle by remember { mutableStateOf("") } // 输入框的文本状态

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. 添加任务区域：输入框 + 按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 输入框
            OutlinedTextField(
                value = newTaskTitle,
                onValueChange = { newTaskTitle = it }, // 实时更新输入文本
                label = { Text("输入新任务...") },
                modifier = Modifier.weight(1f), // 占满剩余宽度
                singleLine = true // 限制单行输入
            )

            // 添加按钮
            Button(
                onClick = {
                    taskViewModel.addTask(newTaskTitle) // 调用ViewModel添加任务
                    newTaskTitle = "" // 清空输入框
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text("添加")
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // 间距

        // 2. 任务列表标题
        Text(
            text = "任务列表",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 3. 任务列表（用LazyColumn高效展示长列表）
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 遍历任务列表，为每个任务创建UI项
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onToggleComplete = { taskViewModel.toggleTaskCompletion(task.id) }, // 切换完成状态
                    onDelete = { taskViewModel.deleteTask(task.id) } // 删除任务
                )
            }
        }
    }
}

@Composable
fun TaskTimeApp() {
    val navController = rememberNavController() // 导航控制器

    // 使用Scaffold布局，底部放导航栏
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        // 页面内容区域（用padding避免被底部导航遮挡）
        NavHost(
            navController = navController,
            startDestination = NavRoutes.TASK_SCREEN, // 默认显示任务页面
            modifier = Modifier.padding(innerPadding)
        ) {
            // 任务页面：关联你的TaskApp组件
            composable(NavRoutes.TASK_SCREEN) {
                TaskApp() // 你的任务列表功能
            }
            // 时间页面：关联新增的TimeScreen组件
            composable(NavRoutes.TIME_SCREEN) {
                TimeScreen() // 时间显示功能
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    // 导航项：包含图标、文字、对应的页面路由
    val items = listOf(
        NavigationItem(
            icon = Icons.Default.Watch,
            label = "时间",
            route = NavRoutes.TIME_SCREEN
        ),
        NavigationItem(
            icon = Icons.Default.List,
            label = "任务",
            route = NavRoutes.TASK_SCREEN
        )
    )

    // 获取当前页面路由，用于高亮选中的导航项
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            )
        }
    }
}

// 新增：导航项数据类（存放图标、文字、路由）
private data class NavigationItem(
    val icon: ImageVector,
    val label: String,
    val route: String
)

// 预览
@Preview(showBackground = true)
@Composable
fun TaskAppPreview() {
    DailyTaskForBettyTheme {
        TaskApp()
    }
}