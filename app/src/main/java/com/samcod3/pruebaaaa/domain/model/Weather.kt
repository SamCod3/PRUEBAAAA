package com.samcod3.pruebaaaa.domain.model

data class Weather(
    val municipioId: String,
    val nombreMunicipio: String,
    val provincia: String,
    val lastUpdated: Long,
    val fecha: String,
    
    // Datos actuales
    val temperaturaActual: Int,
    val temperaturaMax: Int,
    val temperaturaMin: Int,
    val descripcionCielo: String,
    val probabilidadLluvia: Int,
    val vientoVelocidad: Int,
    val vientoDireccion: String,
    
    // Listas de predicción
    val hourlyForecast: List<HourlyForecast> = emptyList(),
    val dailyForecast: List<DailyForecast> = emptyList()
)

data class HourlyForecast(
    val hour: String, // "14:00"
    val temperature: Int,
    val description: String,
    val rainProbability: Int,
    val precipitation: Double = 0.0, // mm de lluvia
    val windSpeed: Int = 0,
    val windDirection: String = ""
)

data class DailyForecast(
    val date: String, // "2025-12-18" formato original para poder parsearlo
    val dateFormatted: String = "", // "JUE 18" para mostrar
    val maxTemp: Int,
    val minTemp: Int,
    val description: String,
    val rainProbability: Int,
    val windSpeed: Int = 0,
    val windDirection: String = "",
    val uvIndex: Int = 0,
    val maxHumidity: Int = 0,
    val minHumidity: Int = 0,
    val maxFeelTemp: Int = 0,
    val minFeelTemp: Int = 0,
    val hourlyDetails: List<HourlyForecast> = emptyList() // Horas de ese día
)