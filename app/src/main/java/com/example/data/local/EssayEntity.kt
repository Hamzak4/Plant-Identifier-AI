package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "essays")
data class EssayEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val topic: String,
    val content: String,
    val language: String = "English",
    val wordCount: Int = 500,
    val educationLevel: String = "University",
    val createdAt: Long = System.currentTimeMillis()
)
