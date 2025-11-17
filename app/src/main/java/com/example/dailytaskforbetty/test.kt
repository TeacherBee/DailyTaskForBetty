package com.example.dailytaskforbetty

import androidx.lifecycle.SharingStarted      // 应该不再红色
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.stateIn

val demo = someFlow.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
)
val x = SharingStarted.WhileSubscribed(5000)