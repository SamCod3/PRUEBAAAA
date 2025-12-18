package com.samcod3.pruebaaaa.data.repository

import android.util.Log
import com.samcod3.pruebaaaa.data.local.dao.WeatherDao
import com.samcod3.pruebaaaa.data.mapper.domain.toDomain
import com.samcod3.pruebaaaa.data.mapper.toEntity
import com.samcod3.pruebaaaa.data.remote.AemetApi
import com.samcod3.pruebaaaa.data.remote.model.DailyForecastDto
import com.samcod3.pruebaaaa.domain.model.Weather
import com.samcod3.pruebaaaa.domain.repository.IWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api: AemetApi,
    private val dao: WeatherDao
) : IWeatherRepository {

    override fun getWeather(municipioId: String): Flow<Weather?> {
        return dao.getWeatherFlow(municipioId).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun refreshWeather(municipioId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Obtener datos horarios y diarios en paralelo
            val hourlyDeferred = async { fetchHourlyData(municipioId) }
            val dailyDeferred = async { fetchDailyData(municipioId) }
            
            val hourlyData = hourlyDeferred.await()
            val dailyData = dailyDeferred.await()
            
            if (hourlyData != null) {
                // Crear entity combinando datos horarios y diarios
                val weatherEntity = hourlyData.toEntity(
                    municipioId = municipioId,
                    dailyForecastDto = dailyData
                )
                
                if (weatherEntity != null) {
                    dao.insertWeather(weatherEntity)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error de mapeo de datos"))
                }
            } else {
                Result.failure(Exception("Datos vacíos o error de mapeo"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    private suspend fun fetchHourlyData(municipioId: String) = try {
        val responseUrl = api.getTownForecastUrl(municipioId)
        if (responseUrl.estado == 200) {
            api.getForecastData(responseUrl.datos).firstOrNull()
        } else {
            Log.e("WeatherRepository", "Error AEMET horario: ${responseUrl.descripcion}")
            null
        }
    } catch (e: Exception) {
        Log.e("WeatherRepository", "Error fetching hourly data", e)
        null
    }
    
    private suspend fun fetchDailyData(municipioId: String): DailyForecastDto? = try {
        val responseUrl = api.getDailyForecastUrl(municipioId)
        if (responseUrl.estado == 200) {
            api.getDailyForecastData(responseUrl.datos).firstOrNull()
        } else {
            Log.e("WeatherRepository", "Error AEMET diario: ${responseUrl.descripcion}")
            null
        }
    } catch (e: Exception) {
        Log.e("WeatherRepository", "Error fetching daily data", e)
        null
    }
}
