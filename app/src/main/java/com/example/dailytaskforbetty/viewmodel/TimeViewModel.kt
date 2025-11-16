package com.example.dailytaskforbetty.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime // 替换java.time.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter // 替换java.time.format.DateTimeFormatter
import org.threeten.bp.ZoneId

class TimeViewModel : ViewModel() {
    private val _currentTime = MutableStateFlow("")
    val currentTime: StateFlow<String> = _currentTime

    init {
        viewModelScope.launch {
            while (true) {
                // 使用ThreeTenABP的API获取当前时间
                val now = LocalDateTime.now(ZoneId.of("Asia/Shanghai"))
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                _currentTime.value = now.format(formatter)

                delay(1000)
            }
        }
    }
}