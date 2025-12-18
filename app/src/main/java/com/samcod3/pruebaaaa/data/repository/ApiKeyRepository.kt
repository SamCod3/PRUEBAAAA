package com.samcod3.pruebaaaa.data.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val KEY_API_TOKEN = "aemet_api_key"

    // Clave por defecto recuperada de los logs para evitar configuración manual
    private val DEFAULT_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFubWVuZG9AZ21haWwuY29tIiwianRpIjoiODllNjg5OTQtYTUzNy00YzE4LWFhNDktNzg1NTgzNzgxMmRhIiwiaXNzIjoiQUVNRVQiLCJpYXQiOjE3NjUyNzM3OTUsInVzZXJJZCI6Ijg5ZTY4OTk0LWE1MzctNGMxOC1hYTQ5LTc4NTU4Mzc4MTJkYSIsInJvbGUiOiIifQ.5L9uevF1IjqptFjGemW8k-EgawfzoveItg6HeaNbdiE" 

    fun getApiKey(): String {
        return prefs.getString(KEY_API_TOKEN, DEFAULT_KEY) ?: DEFAULT_KEY
    }

    fun setApiKey(key: String) {
        prefs.edit().putString(KEY_API_TOKEN, key.trim()).apply()
    }

    fun hasApiKey(): Boolean {
        return getApiKey().isNotEmpty()
    }
}
