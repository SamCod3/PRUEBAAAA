package com.samcod3.pruebaaaa.domain.usecase

import com.samcod3.pruebaaaa.domain.model.Weather
import com.samcod3.pruebaaaa.domain.repository.IWeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val repository: IWeatherRepository
) {
    operator fun invoke(municipioId: String): Flow<Weather?> {
        return repository.getWeather(municipioId)
    }
}
