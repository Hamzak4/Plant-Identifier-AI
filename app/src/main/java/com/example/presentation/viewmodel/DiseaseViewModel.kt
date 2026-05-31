package com.example.presentation.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DiseaseDetectionEntity
import com.example.data.remote.DiseaseDetectionResult
import com.example.data.repository.PlantRepository
import com.example.di.ServiceLocator
import com.example.utils.ImageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface DiseaseUiState {
    object Idle : DiseaseUiState
    object Loading : DiseaseUiState
    data class Success(val result: DiseaseDetectionResult, val imageUri: String) : DiseaseUiState
    data class Error(val error: String) : DiseaseUiState
}

class DiseaseViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: PlantRepository = ServiceLocator.getRepository(application)
) : AndroidViewModel(application) {

    val diseaseHistory: StateFlow<List<DiseaseDetectionEntity>> = repository.getAllDiseases()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow<DiseaseUiState>(DiseaseUiState.Idle)
    val uiState: StateFlow<DiseaseUiState> = _uiState.asStateFlow()

    fun detectDisease(uri: Uri, plantPart: String) {
        viewModelScope.launch {
            _uiState.value = DiseaseUiState.Loading
            try {
                val context = getApplication<Application>()
                val localPath = ImageUtils.saveImageToInternalStorage(context, uri)
                val bitmap = ImageUtils.uriToBitmap(context, uri)
                val base64 = ImageUtils.bitmapToBase64(bitmap)

                val result = repository.detectDisease(base64, plantPart)

                val entity = DiseaseDetectionEntity(
                    plantPart = plantPart,
                    diseaseName = result.diseaseName,
                    severity = result.severity,
                    symptoms = result.symptoms,
                    treatment = result.treatment,
                    confidenceScore = result.confidenceScore,
                    imagePath = localPath,
                    dateDetected = System.currentTimeMillis()
                )
                repository.insertDisease(entity)

                _uiState.value = DiseaseUiState.Success(result, localPath)
            } catch (e: Exception) {
                _uiState.value = DiseaseUiState.Error(e.localizedMessage ?: "Failed analyzing plant disease.")
            }
        }
    }

    fun deleteDetection(entity: DiseaseDetectionEntity) {
        viewModelScope.launch {
            repository.deleteDisease(entity)
        }
    }

    fun resetState() {
        _uiState.value = DiseaseUiState.Idle
    }
}
