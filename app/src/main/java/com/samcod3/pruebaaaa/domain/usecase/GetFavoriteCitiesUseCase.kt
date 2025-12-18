package com.samcod3.pruebaaaa.domain.usecase

import com.samcod3.pruebaaaa.domain.model.City
import com.samcod3.pruebaaaa.domain.repository.ICityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteCitiesUseCase @Inject constructor(
    private val repository: ICityRepository
) {
    operator fun invoke(): Flow<List<City>> {
        return repository.getFavoriteCities()
    }
}
