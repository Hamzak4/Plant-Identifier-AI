package com.example.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.EssayEntity
import com.example.data.repository.PlantRepository
import com.example.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface EssayUiState {
    object Idle : EssayUiState
    object Loading : EssayUiState
    data class Success(val content: String) : EssayUiState
    data class Error(val message: String) : EssayUiState
}

class EssayViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: PlantRepository = ServiceLocator.getRepository(application)
) : AndroidViewModel(application) {

    val savedEssays: StateFlow<List<EssayEntity>> = repository.getAllEssays()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _essayContentState = MutableStateFlow<EssayUiState>(EssayUiState.Idle)
    val essayContentState: StateFlow<EssayUiState> = _essayContentState.asStateFlow()

    fun generateEssay(topic: String, language: String, wordCount: Int, educationLevel: String) {
        viewModelScope.launch {
            _essayContentState.value = EssayUiState.Loading
            try {
                val text = repository.generateEssay(topic, language, wordCount, educationLevel)
                _essayContentState.value = EssayUiState.Success(text)
            } catch (e: Exception) {
                _essayContentState.value = EssayUiState.Error(e.localizedMessage ?: "Failed to generate essay.")
            }
        }
    }

    fun saveEssay(topic: String, content: String, language: String, wordCount: Int, eduLevel: String) {
        viewModelScope.launch {
            try {
                val entity = EssayEntity(
                    topic = topic,
                    content = content,
                    language = language,
                    wordCount = wordCount,
                    educationLevel = eduLevel,
                    createdAt = System.currentTimeMillis()
                )
                repository.insertEssay(entity)
            } catch (e: Exception) {
                // Fail silently
            }
        }
    }

    fun deleteEssay(essay: EssayEntity) {
        viewModelScope.launch {
            try {
                repository.deleteEssay(essay)
            } catch (e: Exception) {
                // Fail silently
            }
        }
    }

    fun resetState() {
        _essayContentState.value = EssayUiState.Idle
    }
}
