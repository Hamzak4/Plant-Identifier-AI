package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val plantName: String,
    val type: String, // "Water Plant", "Fertilizer", "Pruning"
    val intervalDays: Int,
    val lastDoneDate: Long,
    val nextDueDate: Long,
    val isEnabled: Boolean = true
)
