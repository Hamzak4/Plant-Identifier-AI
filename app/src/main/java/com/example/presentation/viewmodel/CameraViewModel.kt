package com.example.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CameraViewModel(application: Application) : AndroidViewModel(application) {
    private val _flashEnabled = MutableStateFlow(false)
    val flashEnabled: StateFlow<Boolean> = _flashEnabled.asStateFlow()

    private val _capturedImageUri = MutableStateFlow<String?>(null)
    val capturedImageUri: StateFlow<String?> = _capturedImageUri.asStateFlow()

    fun toggleFlash() {
        _flashEnabled.value = !_flashEnabled.value
    }

    fun setCapturedImage(imagePath: String?) {
        _capturedImageUri.value = imagePath
    }

    fun resetState() {
        _capturedImageUri.value = null
    }
}
