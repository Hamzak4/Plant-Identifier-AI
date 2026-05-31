package com.example.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiseaseDetectionResult(
    val diseaseName: String,
    val severity: String,
    val symptoms: String,
    val treatment: String,
    val confidenceScore: Double
)
