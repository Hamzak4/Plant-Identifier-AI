package com.example.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PlantEntity
import com.example.data.repository.PlantRepository
import com.example.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: PlantRepository = ServiceLocator.getRepository(application)
) : AndroidViewModel(application) {

    val historyState: StateFlow<List<PlantEntity>> = repository.getAllPlants()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedPlant = MutableStateFlow<PlantEntity?>(null)
    val selectedPlant: StateFlow<PlantEntity?> = _selectedPlant.asStateFlow()

    fun selectPlant(plant: PlantEntity?) {
        _selectedPlant.value = plant
    }

    fun deletePlant(plant: PlantEntity) {
        viewModelScope.launch {
            repository.deletePlant(plant)
            if (_selectedPlant.value?.id == plant.id) {
                _selectedPlant.value = null
            }
        }
    }
}
