package com.samcod3.pruebaaaa.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.samcod3.pruebaaaa.data.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    // Devuelve un Flow: si la DB cambia, se emite un nuevo valor automáticamente
    @Query("SELECT * FROM weather_forecast WHERE municipioId = :municipioId")
    fun getWeatherFlow(municipioId: String): Flow<WeatherEntity?>

    // Devuelve dato puntual (para verificaciones síncronas o oneshot)
    @Query("SELECT * FROM weather_forecast WHERE municipioId = :municipioId")
    suspend fun getWeatherOneShot(municipioId: String): WeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Query("DELETE FROM weather_forecast WHERE lastUpdated < :timestamp")
    suspend fun deleteOldData(timestamp: Long)
}
