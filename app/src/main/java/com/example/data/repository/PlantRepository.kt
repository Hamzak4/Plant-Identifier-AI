package com.example.data.repository

import com.example.data.local.PlantEntity
import com.example.data.local.EssayEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.DiseaseDetectionEntity
import com.example.data.local.ReminderEntity
import com.example.data.remote.PlantDetectionResult
import com.example.data.remote.DiseaseDetectionResult
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    // --- Plants ---
    fun getAllPlants(): Flow<List<PlantEntity>>
    suspend fun insertPlant(plant: PlantEntity): Long
    suspend fun deletePlant(plant: PlantEntity)
    suspend fun getPlantById(id: Int): PlantEntity?
    suspend fun identifyPlant(base64Image: String): PlantDetectionResult

    // --- Essays ---
    fun getAllEssays(): Flow<List<EssayEntity>>
    suspend fun insertEssay(essay: EssayEntity): Long
    suspend fun deleteEssay(essay: EssayEntity)
    suspend fun generateEssay(topic: String, language: String, wordCount: Int, educationLevel: String): String

    // --- Chat Room ---
    fun getAllMessages(): Flow<List<ChatMessageEntity>>
    suspend fun insertMessage(msg: ChatMessageEntity): Long
    suspend fun clearChat()
    suspend fun generateChatResponse(message: String, isExpertMode: Boolean, history: List<ChatMessageEntity>): String

    // --- Disease Detections ---
    fun getAllDiseases(): Flow<List<DiseaseDetectionEntity>>
    suspend fun insertDisease(disease: DiseaseDetectionEntity): Long
    suspend fun deleteDisease(disease: DiseaseDetectionEntity)
    suspend fun detectDisease(base64Image: String, plantPart: String): DiseaseDetectionResult

    // --- Reminders ---
    fun getAllReminders(): Flow<List<ReminderEntity>>
    suspend fun insertReminder(reminder: ReminderEntity): Long
    suspend fun deleteReminder(reminder: ReminderEntity)
}
