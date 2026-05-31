package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val scientificName: String,
    val family: String,
    val description: String,
    val imageUri: String, // Path to local storage or content URI
    val confidence: Double,
    val dateIdentified: Long,
    val careInstructions: String,
    val waterRequirements: String,
    val sunlightRequirements: String,
    val nameUrdu: String = "",
    val familyUrdu: String = "",
    val descriptionUrdu: String = "",
    val careInstructionsUrdu: String = "",
    val waterRequirementsUrdu: String = "",
    val sunlightRequirementsUrdu: String = ""
)
