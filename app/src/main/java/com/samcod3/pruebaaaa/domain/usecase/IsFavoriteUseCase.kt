package com.samcod3.pruebaaaa.domain.usecase

import com.samcod3.pruebaaaa.domain.repository.ICityRepository
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val repository: ICityRepository
) {
    suspend operator fun invoke(cityId: String): Boolean {
        // Obtenemos la ciudad y vemos si es favorita.
        // Ojo: getCityById puede devolver null si no está en el JSON,
        // pero para favoritos debería bastar mirar la tabla.
        // Como getCityById en CityRepositoryImpl ya mira la tabla de favoritos, nos sirve.
        // Pero es un poco indirecto.
        // Idealmente el repositorio debería tener isFavorite(id).
        // Por ahora usaré getCityById.
        return repository.getCityById(cityId)?.isFavorite == true
    }
}
