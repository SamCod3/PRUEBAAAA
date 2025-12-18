package com.samcod3.pruebaaaa.domain.usecase

import com.samcod3.pruebaaaa.domain.model.City
import com.samcod3.pruebaaaa.domain.repository.ICityRepository
import javax.inject.Inject

class SearchCityUseCase @Inject constructor(
    private val repository: ICityRepository
) {
    suspend operator fun invoke(query: String): List<City> {
        if (query.isBlank()) return emptyList()
        return repository.searchCity(query)
    }
}
