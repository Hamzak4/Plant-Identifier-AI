package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    // --- Plants ---
    @Query("SELECT * FROM plants ORDER BY dateIdentified DESC")
    fun getAllPlants(): Flow<List<PlantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantEntity): Long

    @Delete
    suspend fun deletePlant(plant: PlantEntity)

    @Query("SELECT * FROM plants WHERE id = :id")
    suspend fun getPlantById(id: Int): PlantEntity?

    // --- Essays ---
    @Query("SELECT * FROM essays ORDER BY createdAt DESC")
    fun getAllEssays(): Flow<List<EssayEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEssay(essay: EssayEntity): Long

    @Delete
    suspend fun deleteEssay(essay: EssayEntity)

    // --- Chat Messages ---
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(msg: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearChat()

    // --- Disease Detections ---
    @Query("SELECT * FROM disease_detections ORDER BY dateDetected DESC")
    fun getAllDiseases(): Flow<List<DiseaseDetectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDisease(disease: DiseaseDetectionEntity): Long

    @Delete
    suspend fun deleteDisease(disease: DiseaseDetectionEntity)

    // --- Reminders ---
    @Query("SELECT * FROM reminders ORDER BY nextDueDate ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)
}
