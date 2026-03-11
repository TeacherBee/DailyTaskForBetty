package com.example.dailytaskforbetty.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "retroactive_history")
data class RetroactiveHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: String,
    val taskTitle: String,
    val retroactiveDate: Long,
    val year: Int,
    val month: Int
)
