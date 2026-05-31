package com.example.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AuthRepository
import com.example.data.repository.UserSession
import com.example.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    object Success : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel @JvmOverloads constructor(
    application: Application,
    private val authRepository: AuthRepository = ServiceLocator.getAuthRepository(application)
) : AndroidViewModel(application) {

    val userSession: StateFlow<UserSession?> = authRepository.currentUserSession

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    fun login(email: String, password: String, rememberMe: Boolean, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.login(email, password, rememberMe)
            if (result.isSuccess) {
                _uiState.value = AuthUiState.Success
                onSuccess()
            } else {
                _uiState.value = AuthUiState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun loginWithGoogle(
        idToken: String,
        email: String,
        displayName: String,
        rememberMe: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.loginWithGoogle(idToken, email, displayName, rememberMe)
            if (result.isSuccess) {
                _uiState.value = AuthUiState.Success
                onSuccess()
            } else {
                _uiState.value = AuthUiState.Error(result.exceptionOrNull()?.message ?: "Google Login failed")
            }
        }
    }

    fun register(fullName: String, email: String, password: String, rememberMe: Boolean, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.register(fullName, email, password, rememberMe)
            if (result.isSuccess) {
                _uiState.value = AuthUiState.Success
                onSuccess()
            } else {
                _uiState.value = AuthUiState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun forgotPassword(email: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.forgotPassword(email)
            if (result.isSuccess) {
                _uiState.value = AuthUiState.Success
                onSuccess()
            } else {
                _uiState.value = AuthUiState.Error(result.exceptionOrNull()?.message ?: "Password reset failed")
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }

    fun updateProfileName(newName: String) {
        authRepository.updateProfileName(newName)
    }
}
