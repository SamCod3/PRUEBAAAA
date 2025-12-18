package com.samcod3.pruebaaaa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.samcod3.pruebaaaa.domain.model.DailyForecast
import com.samcod3.pruebaaaa.domain.model.HourlyForecast

@Entity(tableName = "weather_forecast")
data class WeatherEntity(
    @PrimaryKey
    val municipioId: String, // Ej: "28079"
    val nombreMunicipio: String,
    val provincia: String,
    val lastUpdated: Long, // Timestamp del momento de guardado
    
    // Predicción simplificada (solo lo vital para la UI principal)
    val fecha: String, // YYYY-MM-DD
    val temperaturaMax: Int,
    val temperaturaMin: Int,
    val temperaturaActual: Int, // Estimada o último dato
    val descripcionCielo: String, // "Despejado", "Nuboso"
    val estadoCieloCode: String?, // Para buscar icono
    val probabilidadLluvia: Int, // %
    val vientoVelocidad: Int, // km/h
    val vientoDireccion: String?, // N, S, E, O...

    // Nuevos campos para detalle
    val hourlyForecast: List<HourlyForecast> = emptyList(),
    val dailyForecast: List<DailyForecast> = emptyList()
)
