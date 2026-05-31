package com.example.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ChatMessageEntity
import com.example.data.repository.PlantRepository
import com.example.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: PlantRepository = ServiceLocator.getRepository(application)
) : AndroidViewModel(application) {

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.getAllMessages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _isExpertMode = MutableStateFlow(true) // Professional Botanist
    val isExpertMode: StateFlow<Boolean> = _isExpertMode.asStateFlow()

    fun toggleExpertMode() {
        _isExpertMode.value = !_isExpertMode.value
    }

    fun sendMessage(text: String, imageUri: String? = null) {
        if (text.isBlank() && imageUri == null) return

        viewModelScope.launch {
            val userMsg = ChatMessageEntity(
                sender = "user",
                text = text,
                imageUri = imageUri,
                timestamp = System.currentTimeMillis()
            )
            repository.insertMessage(userMsg)

            _isTyping.value = true

            try {
                val currentHistory = chatMessages.value
                val aiResponse = repository.generateChatResponse(text, _isExpertMode.value, currentHistory)

                val aiMsg = ChatMessageEntity(
                    sender = "ai",
                    text = aiResponse,
                    timestamp = System.currentTimeMillis()
                )
                repository.insertMessage(aiMsg)
            } catch (e: Exception) {
                val errorMsg = ChatMessageEntity(
                    sender = "ai",
                    text = "Unable to process message. Please check your internet connectivity or confirm your API key is correct. \n\nDetails: ${e.localizedMessage}",
                    timestamp = System.currentTimeMillis()
                )
                repository.insertMessage(errorMsg)
            } finally {
                _isTyping.value = false
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }
}
