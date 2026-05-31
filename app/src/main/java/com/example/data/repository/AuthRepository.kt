package com.example.data.repository

import kotlinx.coroutines.flow.StateFlow

data class UserSession(
    val fullName: String,
    val email: String,
    val isLoggedIn: Boolean,
    val isFirebaseMode: Boolean
)

interface AuthRepository {
    val currentUserSession: StateFlow<UserSession?>
    fun checkAutoLogin()
    suspend fun register(fullName: String, email: String, password: String, rememberMe: Boolean): Result<Unit>
    suspend fun login(email: String, password: String, rememberMe: Boolean): Result<Unit>
    suspend fun loginWithGoogle(idToken: String, email: String, displayName: String, rememberMe: Boolean): Result<Unit>
    suspend fun forgotPassword(email: String): Result<Unit>
    fun logout()
    fun updateProfileName(newName: String)
}
