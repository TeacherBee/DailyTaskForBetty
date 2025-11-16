// 任务列表页面组件

package com.example.dailytaskforbetty.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailytaskforbetty.model.Task
import com.example.dailytaskforbetty.viewmodel.TaskViewModel
import androidx.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.*

// 任务列表页面组件
@Composable
fun TaskScreen(
    taskViewModel: TaskViewModel = viewModel() // 使用之前的TaskViewModel
) {
    val tasks by taskViewModel.tasks.collectAsState()

    // 格式化时间的工具（北京时间）
    val timeFormatter = remember {
        SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).apply {
            timeZone = TimeZone.getTimeZone("Asia/Shanghai")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 页面标题
        Text(
            text = "每日任务",
            style = MaterialTheme.typography.titleLarge, // 已自带 Bold
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 预设任务列表（移除用户添加区域）
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tasks) { task ->
                PresetTaskItem(
                    task = task,
                    formattedNextRefresh = timeFormatter.format(task.nextRefreshTime),
                    onComplete = { taskViewModel.completeTask(task.id) }
                )
            }
        }
    }
}

// 预设任务项UI（显示标题、奖励、下次刷新时间、完成按钮）
@Composable
internal fun PresetTaskItem(
    task: Task,
    formattedNextRefresh: String, // 格式化后的下次刷新时间
    onComplete: () -> Unit
) {
    val isCompleted = task.isCompleted
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 任务信息（标题、奖励、下次刷新）
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "奖励：${task.reward} 积分",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCompleted) Color.Gray else Color(0xFF00C853)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "下次刷新：$formattedNextRefresh",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 完成按钮（已完成则禁用）
            Button(
                onClick = onComplete,
                enabled = !isCompleted,
                modifier = Modifier.size(width = 100.dp, height = 40.dp)
            ) {
                Text(if (isCompleted) "已完成" else "完成")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskScreenPreview() {
    MaterialTheme {
        TaskScreen()
    }
}