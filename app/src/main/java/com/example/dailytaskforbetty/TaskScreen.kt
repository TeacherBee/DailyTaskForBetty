// 任务列表页面组件

package com.example.dailytaskforbetty

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

// 任务列表页面组件
@Composable
fun TaskScreen(
    viewModel: TaskViewModel = viewModel() // 使用之前的TaskViewModel
) {
    val tasks by viewModel.tasks.collectAsState()
    var newTaskTitle by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 添加任务区域
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = newTaskTitle,
                onValueChange = { newTaskTitle = it },
                label = { Text("输入新任务...") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Button(
                onClick = {
                    viewModel.addTask(newTaskTitle)
                    newTaskTitle = ""
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text("添加")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 任务列表标题
        Text(
            text = "任务列表",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 任务列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onToggleComplete = { viewModel.toggleTaskCompletion(task.id) },
                    onDelete = { viewModel.deleteTask(task.id) }
                )
            }
        }
    }
}

// 单个任务项（复用）
// 单个任务项的UI
@Composable
fun TaskItem(
    task: Task,
    onToggleComplete: () -> Unit, // 切换完成状态的回调
    onDelete: () -> Unit // 删除任务的回调
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 复选框：标记完成/未完成
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onToggleComplete() } // 点击时触发回调
        )

        // 任务标题：完成的任务显示删除线
        Text(
            text = task.title,
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            color = if (task.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        // 删除按钮
        IconButton(onClick = { onDelete() }) {
            Image(
                imageVector = Icons.Default.Delete,
                contentDescription = "删除任务",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}