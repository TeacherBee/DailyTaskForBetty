package com.example.dailytaskforbetty.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime // 替换java.time.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter // 替换java.time.format.DateTimeFormatter
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit

class TimeViewModel : ViewModel() {
    private val _currentTime = MutableStateFlow("")
    val currentTime: StateFlow<String> = _currentTime

    // 恋爱天数状态
    private val _loveDays = MutableStateFlow(0)
    val loveDays: StateFlow<Int> = _loveDays

    // 恋爱开始日期：2023年3月14日
    private val loveStartDate = LocalDate.of(2023, 3, 14)

    init {
        viewModelScope.launch {
            while (true) {
                // 使用ThreeTenABP的API获取当前时间
                val now = LocalDateTime.now(ZoneId.of("Asia/Shanghai"))
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                _currentTime.value = now.format(formatter)

                // 计算恋爱天数
                val currentDate = LocalDate.now(ZoneId.of("Asia/Shanghai"))
                val days = ChronoUnit.DAYS.between(loveStartDate, currentDate)
                _loveDays.value = days.toInt()

                delay(1000)
            }
        }
    }
}