package com.orbit.recovery.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orbit.recovery.data.UserPreferencesRepository
import com.orbit.recovery.data.dataStore
import com.orbit.recovery.data.remote.OrbitAiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ChatMessage(val role: String, val content: String)

class CoachViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserPreferencesRepository(application.dataStore)
    private val gson = Gson()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            val prefs = repository.preferencesFlow.first()
            val savedMessagesJson = prefs[UserPreferencesRepository.COACH_MESSAGES_KEY] ?: "[]"
            val type = object : TypeToken<List<ChatMessage>>() {}.type
            var loadedMessages: List<ChatMessage> = gson.fromJson(savedMessagesJson, type) ?: emptyList()
            
            if (loadedMessages.isEmpty()) {
                val savedName = prefs[UserPreferencesRepository.NAME_KEY]?.trim()
                val userName = if (savedName.isNullOrBlank() || savedName.length > 20) "there" else savedName
                loadedMessages = listOf(
                    ChatMessage(
                        role = "assistant",
                        content = "Hi $userName, I'm your Orbit Coach. I'm here whenever you need a calm place to think things through. How are you feeling right now?"
                    )
                )
                saveMessages(loadedMessages)
            }
            
            _messages.value = loadedMessages
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || _isLoading.value) return

        viewModelScope.launch {
            val currentList = _messages.value.toMutableList()
            currentList.add(ChatMessage(role = "user", content = text))
            _messages.value = currentList
            saveMessages(currentList)

            _isLoading.value = true

            try {
                val systemPrompt = """
                    You are Orbit, a warm and non-judgmental recovery coach helping someone 
                    overcome compulsive habits. You speak in short, calm, supportive sentences. 
                    You never shame the user. You celebrate small wins. When someone is 
                    struggling, you guide them toward their next small right action — 
                    never toward perfection. Keep responses under 150 words.
                """.trimIndent()
        
                val reply = OrbitAiService.sendMessage(
                    systemPrompt = systemPrompt,
                    conversationHistory = _messages.value
                        .dropLast(1)
                        .map { Pair(if (it.role == "user") "user" else "assistant", it.content) },
                    userMessage = text
                )
                
                val newList = _messages.value.toMutableList()
                newList.add(ChatMessage(role = "assistant", content = reply))
                _messages.value = newList
                saveMessages(newList)
            } catch (e: Exception) {
                val newList = _messages.value.toMutableList()
                newList.add(ChatMessage(
                    role = "assistant",
                    content = "I'm here — just having a moment of trouble connecting. Try again?"
                ))
                _messages.value = newList
                saveMessages(newList)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun saveMessages(msgs: List<ChatMessage>) {
        val limitedMessages = if (msgs.size > 50) msgs.takeLast(50) else msgs
        val json = gson.toJson(limitedMessages)
        repository.saveCoachMessages(json)
    }
}
