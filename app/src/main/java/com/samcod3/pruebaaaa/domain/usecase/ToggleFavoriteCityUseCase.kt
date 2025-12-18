package com.samcod3.pruebaaaa.domain.usecase

import com.samcod3.pruebaaaa.domain.model.City
import com.samcod3.pruebaaaa.domain.repository.ICityRepository
import javax.inject.Inject

class ToggleFavoriteCityUseCase @Inject constructor(
    private val repository: ICityRepository
) {
    suspend operator fun invoke(city: City) {
        repository.toggleFavorite(city)
    }
}
