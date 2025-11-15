// 任务栏类

package com.example.dailytaskforbetty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID // 用于生成唯一ID

class TaskViewModel : ViewModel() {
    // 私有可变状态（仅ViewModel内部修改）
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    // 暴露给UI的不可变状态（UI只能读取）
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    // 添加新任务（传入标题，自动生成ID，默认未完成）
    fun addTask(title: String) {
        if (title.isNotBlank()) { // 避免空任务
            val newTask = Task(
                id = UUID.randomUUID().toString(), // 生成唯一ID
                title = title,
                isCompleted = false
            )
            // 在ViewModel作用域中更新状态（安全线程）
            viewModelScope.launch {
                _tasks.value = _tasks.value + newTask
            }
        }
    }

    // 切换任务的完成状态（根据ID找到任务，反转isCompleted）
    fun toggleTaskCompletion(taskId: String) {
        viewModelScope.launch {
            _tasks.value = _tasks.value.map { task ->
                if (task.id == taskId) {
                    task.copy(isCompleted = !task.isCompleted) // 复制并修改状态
                } else {
                    task
                }
            }
        }
    }

    // 删除任务（根据ID过滤掉要删除的任务）
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            _tasks.value = _tasks.value.filter { it.id != taskId }
        }
    }
}