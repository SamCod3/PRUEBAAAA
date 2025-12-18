package com.samcod3.pruebaaaa.domain.repository

import com.samcod3.pruebaaaa.domain.model.Weather
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    fun getWeather(municipioId: String): Flow<Weather?>
    suspend fun refreshWeather(municipioId: String): Result<Unit>
}
