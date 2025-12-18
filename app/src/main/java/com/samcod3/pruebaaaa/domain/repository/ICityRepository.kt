package com.samcod3.pruebaaaa.domain.repository

import com.samcod3.pruebaaaa.domain.model.City
import kotlinx.coroutines.flow.Flow

interface ICityRepository {
    suspend fun searchCity(query: String): List<City>
    suspend fun getCityById(id: String): City?
    fun getFavoriteCities(): Flow<List<City>>
    suspend fun toggleFavorite(city: City)
}
