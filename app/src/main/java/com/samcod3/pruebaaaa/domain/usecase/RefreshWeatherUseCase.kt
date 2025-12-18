package com.samcod3.pruebaaaa.domain.usecase

import com.samcod3.pruebaaaa.domain.repository.IWeatherRepository
import javax.inject.Inject

class RefreshWeatherUseCase @Inject constructor(
    private val repository: IWeatherRepository
) {
    suspend operator fun invoke(municipioId: String): Result<Unit> {
        return repository.refreshWeather(municipioId)
    }
}
