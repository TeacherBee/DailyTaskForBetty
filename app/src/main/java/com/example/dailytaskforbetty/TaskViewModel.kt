// 任务栏类

package com.example.dailytaskforbetty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID // 用于生成唯一ID
import kotlin.random.Random

class TaskViewModel : ViewModel() {
    // 私有可变状态（仅ViewModel内部修改）
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    // 暴露给UI的不可变状态（UI只能读取）
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    // 总奖励状态（累计完成任务的奖励）
    private val _totalReward = MutableStateFlow(0)
    val totalReward: StateFlow<Int> = _totalReward.asStateFlow()

    // 添加新任务（传入标题，自动生成ID，默认未完成）
    fun addTask(title: String) {
        if (title.isNotBlank()) { // 避免空任务
            val newTask = Task(
                id = UUID.randomUUID().toString(), // 生成唯一ID
                title = title,
                isCompleted = false,
                reward = Random.nextInt(1, 11) // 1-10随机奖励
            )
            // 在ViewModel作用域中更新状态（安全线程）
            viewModelScope.launch {
                _tasks.value = _tasks.value + newTask
                // 调试用：添加后打印奖励（可选，确认奖励生成正常）
                println("新增任务奖励：${newTask.reward}")
            }
        }
    }

    // 切换任务的完成状态（根据ID找到任务，反转isCompleted）
    fun toggleTaskCompletion(taskId: String) {
        viewModelScope.launch {
            _tasks.value = _tasks.value.map { task ->
                if (task.id == taskId && !task.isCompleted) {
                    // 累加奖励
                    _totalReward.value += task.reward
                    // 调试用：打印累加的奖励（可选）
                    val addedReward = task.reward
                    println("累加奖励：$addedReward，当前总奖励：${_totalReward.value}")
                    // 标记为完成
                    task.copy(isCompleted = true)
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