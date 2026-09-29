package com.orbit.recovery.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object OrbitAiService {

    private const val BASE_URL = "https://openrouter.ai/api/v1/chat/completions"
    private const val MODEL = "qwen/qwen3-8b:free"
    private const val API_KEY = "YOUR_API_KEY_HERE"

    suspend fun sendMessage(
        systemPrompt: String,
        conversationHistory: List<Pair<String, String>> = emptyList(),
        userMessage: String
    ): String = withContext(Dispatchers.IO) {
        try {
            val url = URL(BASE_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Authorization", "Bearer $API_KEY")
            connection.setRequestProperty("HTTP-Referer", "com.orbit.recovery")
            connection.setRequestProperty("X-Title", "Orbit Recovery App")
            connection.connectTimeout = 30000
            connection.readTimeout = 30000
            connection.doOutput = true

            val messages = JSONArray()

            // System prompt
            messages.put(JSONObject().apply {
                put("role", "system")
                put("content", systemPrompt)
            })

            // Conversation history for context
            conversationHistory.forEach { (role, content) ->
                messages.put(JSONObject().apply {
                    put("role", role)
                    put("content", content)
                })
            }

            // Latest user message
            messages.put(JSONObject().apply {
                put("role", "user")
                put("content", userMessage)
            })

            val requestBody = JSONObject().apply {
                put("model", MODEL)
                put("messages", messages)
                put("max_tokens", 1000)
                put("temperature", 0.7)
            }.toString()

            connection.outputStream.bufferedWriter().use { it.write(requestBody) }

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                val error = connection.errorStream?.bufferedReader()?.readText()
                android.util.Log.e("GemmaAPI", "Error $responseCode: $error")
                return@withContext "Connection error $responseCode. Check your API key and internet."
            }

            val response = connection.inputStream.bufferedReader().readText()
            val json = JSONObject(response)
            json.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim()
        } catch (e: Exception) {
            "I'm having trouble connecting right now. Please try again in a moment."
        }
    }
}
