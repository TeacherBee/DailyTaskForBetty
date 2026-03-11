package com.example.dailytaskforbetty.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailytaskforbetty.model.Task
import com.example.dailytaskforbetty.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(
    taskViewModel: TaskViewModel = viewModel()
) {
    val tasks by taskViewModel.tasks.collectAsState()
    
    // 当前显示的月份
    var currentMonth by remember { mutableStateOf(Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"))) }
    // 选中的日期
    var selectedDate by remember { mutableStateOf<Calendar?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 页面标题
        Text(
            text = "日历",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 月份导航
        MonthNavigation(
            currentMonth = currentMonth,
            onPreviousMonth = {
                val newMonth = currentMonth.clone() as Calendar
                newMonth.add(Calendar.MONTH, -1)
                currentMonth = newMonth
            },
            onNextMonth = {
                val newMonth = currentMonth.clone() as Calendar
                newMonth.add(Calendar.MONTH, 1)
                currentMonth = newMonth
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 日历视图
        CalendarView(
            currentMonth = currentMonth,
            selectedDate = selectedDate,
            tasks = tasks,
            onDateSelected = { date ->
                selectedDate = date
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 选中日期的任务列表
        selectedDate?.let { date ->
            SelectedDateTasks(
                date = date,
                tasks = tasks,
                taskViewModel = taskViewModel,
                onCompleteTask = { taskId ->
                    taskViewModel.completeTask(taskId)
                },
                onRetroactiveComplete = { taskId ->
                    taskViewModel.completeTaskRetroactively(taskId, date)
                }
            )
        }
    }
}

@Composable
private fun MonthNavigation(
    currentMonth: Calendar,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val monthFormat = SimpleDateFormat("yyyy年MM月", Locale.CHINA).apply {
        timeZone = TimeZone.getTimeZone("Asia/Shanghai")
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onPreviousMonth,
            modifier = Modifier.size(40.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("<")
        }
        
        Text(
            text = monthFormat.format(currentMonth.time),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Button(
            onClick = onNextMonth,
            modifier = Modifier.size(40.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(">")
        }
    }
}

@Composable
private fun CalendarView(
    currentMonth: Calendar,
    selectedDate: Calendar?,
    tasks: List<Task>,
    onDateSelected: (Calendar) -> Unit
) {
    // 星期标题
    val weekDays = listOf("日", "一", "二", "三", "四", "五", "六")
    
    Column {
        // 星期标题行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekDays.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 日期网格
        val dates = getDatesForMonth(currentMonth)
        val today = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"))
        
        dates.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                week.forEach { date ->
                    if (date != null) {
                        val isToday = isSameDay(date, today)
                        val isSelected = selectedDate?.let { isSameDay(date, it) } ?: false
                        val hasCompletedTask = hasTaskOnDate(date, tasks, true)
                        val hasIncompleteTask = hasTaskOnDate(date, tasks, false)
                        
                        DateCell(
                            date = date,
                            isToday = isToday,
                            isSelected = isSelected,
                            hasCompletedTask = hasCompletedTask,
                            hasIncompleteTask = hasIncompleteTask,
                            onClick = { onDateSelected(date) }
                        )
                    } else {
                        // 空日期也需要占位，保持宽度一致
                        Spacer(modifier = Modifier.width(40.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DateCell(
    date: Calendar,
    isToday: Boolean,
    isSelected: Boolean,
    hasCompletedTask: Boolean,
    hasIncompleteTask: Boolean,
    onClick: () -> Unit
) {
    val dayOfMonth = date.get(Calendar.DAY_OF_MONTH)
    
    Box(
        modifier = Modifier
            .width(40.dp)
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // 选中或今天的背景
        val backgroundColor = when {
            isSelected -> MaterialTheme.colorScheme.primary
            isToday -> MaterialTheme.colorScheme.primaryContainer
            else -> Color.Transparent
        }
        
        val textColor = when {
            isSelected -> MaterialTheme.colorScheme.onPrimary
            else -> MaterialTheme.colorScheme.onSurface
        }
        
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = backgroundColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
        
        // 任务完成指示器
        if (hasCompletedTask || hasIncompleteTask) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        color = if (hasCompletedTask) Color(0xFF4CAF50) else Color(0xFFFF9800),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun SelectedDateTasks(
    date: Calendar,
    tasks: List<Task>,
    taskViewModel: TaskViewModel,
    onCompleteTask: (String) -> Unit,
    onRetroactiveComplete: (String) -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA).apply {
        timeZone = TimeZone.getTimeZone("Asia/Shanghai")
    }
    
    val tasksOnDate = getTasksForDate(date, tasks)
    val today = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"))
    val isToday = isSameDay(date, today)
    val isPastDate = date.before(today) && !isToday
    val isFutureDate = date.after(today) && !isToday
    
    Column {
        Text(
            text = "${dateFormat.format(date.time)}的任务",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        if (tasksOnDate.isEmpty()) {
            Text(
                text = "当天没有任务",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tasksOnDate) { task ->
                    CalendarTaskItem(
                        task = task,
                        isToday = isToday,
                        isPastDate = isPastDate,
                        isFutureDate = isFutureDate,
                        selectedDate = date,
                        taskViewModel = taskViewModel,
                        onRetroactiveComplete = { onRetroactiveComplete(task.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarTaskItem(
    task: Task,
    isToday: Boolean,
    isPastDate: Boolean,
    isFutureDate: Boolean,
    selectedDate: Calendar,
    taskViewModel: TaskViewModel,
    onRetroactiveComplete: () -> Unit
) {
    // 可以补领的任务ID列表（喝水和喝蜂蜜水）
    val retroactiveTaskIds = listOf("task_drink", "task_drink_plus")
    // 一周不吃xx任务ID
    val sundayOnlyTaskId = "task_no_xx"
    
    // 补领次数（通过LaunchedEffect从数据库获取）
    var retroactiveCount by remember { mutableStateOf(0) }
    // 任务在该日期是否补领过
    var isRetroactiveOnDate by remember { mutableStateOf(false) }
    // 检查选中的日期是否是周日
    val isSunday = taskViewModel.isSunday(selectedDate)
    
    // 获取年份和月份
    val year = selectedDate.get(Calendar.YEAR)
    val month = selectedDate.get(Calendar.MONTH) + 1
    
    // 从数据库获取补领次数和检查是否补领过
    LaunchedEffect(task.id, year, month, selectedDate.timeInMillis) {
        retroactiveCount = taskViewModel.getRetroactiveCount(task.id, year, month)
        isRetroactiveOnDate = taskViewModel.isTaskRetroactiveOnDate(task.id, selectedDate)
    }
    
    // 判断任务在选中日期是否完成（检查lastCompletedTime或补领历史）
    val isCompletedOnDate = when {
        isFutureDate -> false
        isToday -> task.isCompleted
        else -> {
            val isCompletedByLastTime = task.lastCompletedTime?.let { lastCompleted ->
                val completedCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"))
                completedCal.time = lastCompleted
                isSameDay(selectedDate, completedCal)
            } ?: false
            isCompletedByLastTime || isRetroactiveOnDate
        }
    }
    
    // 喝水/喝蜂蜜水：可以补领，每月最多5次
    val isNormalRetroactiveTask = isPastDate && !isCompletedOnDate && 
            retroactiveTaskIds.contains(task.id) && retroactiveCount < 5
    
    // 一周不吃xx：可以补领，但只能在周日，无次数限制
    val isSundayOnlyRetroactiveTask = isPastDate && !isCompletedOnDate && 
            task.id == sundayOnlyTaskId && isSunday
    
    val canRetroactive = isNormalRetroactiveTask || isSundayOnlyRetroactiveTask
    
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isCompletedOnDate) Color.Gray else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "奖励：${task.reward} 积分",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCompletedOnDate) Color.Gray else Color(0xFF00C853)
                )
                // 只有喝水/喝蜂蜜水任务才显示补领次数
                if (isPastDate && !isCompletedOnDate && retroactiveTaskIds.contains(task.id)) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "本月补领次数：$retroactiveCount/5",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            
            when {
                isCompletedOnDate -> {
                    Text(
                        text = "已完成",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                canRetroactive -> {
                    Button(
                        onClick = {
                            onRetroactiveComplete()
                            // 只有喝水/喝蜂蜜水任务才增加补领次数
                            if (retroactiveTaskIds.contains(task.id)) {
                                retroactiveCount++
                            }
                            isRetroactiveOnDate = true
                        },
                        modifier = Modifier.size(width = 100.dp, height = 40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text("补领")
                    }
                }
                else -> {
                    Text(
                        text = "未完成",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// 辅助函数：获取某个月的所有日期（包含空值用于填充）
private fun getDatesForMonth(calendar: Calendar): List<Calendar?> {
    val result = mutableListOf<Calendar?>()
    
    val cal = calendar.clone() as Calendar
    cal.set(Calendar.DAY_OF_MONTH, 1)
    
    // 填充月份开始前的空白
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    for (i in 1 until firstDayOfWeek) {
        result.add(null)
    }
    
    // 添加月份中的所有日期
    val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    for (day in 1..maxDay) {
        val dateCal = cal.clone() as Calendar
        dateCal.set(Calendar.DAY_OF_MONTH, day)
        result.add(dateCal)
    }
    
    return result
}

// 辅助函数：判断两个日期是否是同一天
private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}

// 辅助函数：判断某个日期是否有任务
private fun hasTaskOnDate(date: Calendar, tasks: List<Task>, completed: Boolean): Boolean {
    return tasks.any { task ->
        if (task.isCompleted != completed) return@any false
        
        task.lastCompletedTime?.let { lastCompleted ->
            val completedCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"))
            completedCal.time = lastCompleted
            isSameDay(date, completedCal)
        } ?: false
    }
}

// 辅助函数：获取某个日期的任务
private fun getTasksForDate(date: Calendar, tasks: List<Task>): List<Task> {
    return tasks.filter { task ->
        // 简化逻辑：显示所有任务，但标记完成状态
        // 更精确的逻辑可以根据任务周期和完成时间来判断
        true
    }
}
