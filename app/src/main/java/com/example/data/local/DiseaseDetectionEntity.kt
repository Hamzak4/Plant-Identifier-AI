package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disease_detections")
data class DiseaseDetectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val plantPart: String,
    val diseaseName: String,
    val severity: String,
    val symptoms: String,
    val treatment: String,
    val confidenceScore: Double,
    val imagePath: String,
    val dateDetected: Long = System.currentTimeMillis()
)
