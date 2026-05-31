package com.example.presentation.viewmodel

import android.app.Application
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PlantEntity
import com.example.data.remote.PlantDetectionResult
import com.example.data.repository.PlantRepository
import com.example.di.ServiceLocator
import com.example.utils.ImageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed interface PlantUiState {
    object Idle : PlantUiState
    object Loading : PlantUiState
    data class Success(val result: PlantDetectionResult, val localImagePath: String) : PlantUiState
    data class Error(val error: String) : PlantUiState
}

class PlantViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: PlantRepository = ServiceLocator.getRepository(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<PlantUiState>(PlantUiState.Idle)
    val uiState: StateFlow<PlantUiState> = _uiState.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    fun identifyPlantFromPath(localImagePath: String) {
        viewModelScope.launch {
            _uiState.value = PlantUiState.Loading
            _isSaved.value = false
            try {
                val file = File(localImagePath)
                if (!file.exists()) {
                    _uiState.value = PlantUiState.Error("Captured image file not found.")
                    return@launch
                }
                
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    ?: throw java.lang.IllegalStateException("Failed to decode the captured image.")
                
                val base64 = ImageUtils.bitmapToBase64(bitmap)
                val result = repository.identifyPlant(base64)
                
                _uiState.value = PlantUiState.Success(result, localImagePath)
            } catch (e: Exception) {
                _uiState.value = PlantUiState.Error(e.localizedMessage ?: "An error occurred during plant identification.")
            }
        }
    }

    fun identifyPlantFromUri(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = PlantUiState.Loading
            _isSaved.value = false
            try {
                val context = getApplication<Application>()
                val localPath = ImageUtils.saveImageToInternalStorage(context, uri)
                val bitmap = ImageUtils.uriToBitmap(context, uri)
                val base64 = ImageUtils.bitmapToBase64(bitmap)
                
                val result = repository.identifyPlant(base64)
                _uiState.value = PlantUiState.Success(result, localPath)
            } catch (e: Exception) {
                _uiState.value = PlantUiState.Error(e.localizedMessage ?: "An error occurred during gallery image analysis.")
            }
        }
    }

    fun savePlantToHistory(result: PlantDetectionResult, localImagePath: String) {
        viewModelScope.launch {
            try {
                val entity = PlantEntity(
                    name = result.name,
                    scientificName = result.scientificName,
                    family = result.family,
                    description = result.description,
                    imageUri = localImagePath,
                    confidence = result.confidenceScore,
                    dateIdentified = System.currentTimeMillis(),
                    careInstructions = result.careInstructions,
                    waterRequirements = result.waterRequirements,
                    sunlightRequirements = result.sunlightRequirements
                )
                repository.insertPlant(entity)
                _isSaved.value = true
            } catch (e: Exception) {
                _uiState.value = PlantUiState.Error("Failed to save plant: ${e.localizedMessage}")
            }
        }
    }

    fun resetState() {
        _uiState.value = PlantUiState.Idle
        _isSaved.value = false
    }
}
