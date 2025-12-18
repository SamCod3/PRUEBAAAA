package com.samcod3.pruebaaaa.data.repository

import com.samcod3.pruebaaaa.data.local.dao.FavoriteCityDao
import com.samcod3.pruebaaaa.data.local.entity.FavoriteCityEntity
import com.samcod3.pruebaaaa.domain.model.City
import com.samcod3.pruebaaaa.domain.repository.ICityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CityRepositoryImpl @Inject constructor(
    private val municipiosRepository: MunicipiosRepository,
    private val favoriteDao: FavoriteCityDao
) : ICityRepository {

    override suspend fun searchCity(query: String): List<City> {
        val municipios = municipiosRepository.searchMunicipios(query)
        val favoriteIds = favoriteDao.getFavoriteIds().toSet()

        return municipios.map { dto ->
            val cleanId = dto.getCleanId()
            City(
                id = cleanId,
                nombre = dto.nombre,
                provincia = dto.getProvinciaName(),
                isFavorite = favoriteIds.contains(cleanId)
            )
        }
    }

    override suspend fun getCityById(id: String): City? {
        val municipios = municipiosRepository.getMunicipios()
        val municipio = municipios.find { 
            it.getCleanId() == id || it.id == "id$id" 
        }
        
        return municipio?.let {
            City(
                id = it.getCleanId(),
                nombre = it.nombre,
                provincia = it.getProvinciaName(),
                isFavorite = favoriteDao.isFavorite(it.getCleanId())
            )
        }
    }

    override fun getFavoriteCities(): Flow<List<City>> {
        return favoriteDao.getFavorites().map { entities ->
            entities.map { entity ->
                City(
                    id = entity.id,
                    nombre = entity.nombre,
                    provincia = entity.provincia,
                    isFavorite = true
                )
            }
        }
    }

    override suspend fun toggleFavorite(city: City) {
        if (city.isFavorite) {
            favoriteDao.deleteFavoriteById(city.id)
        } else {
            favoriteDao.insertFavorite(
                FavoriteCityEntity(
                    id = city.id,
                    nombre = city.nombre,
                    provincia = city.provincia
                )
            )
        }
    }
}
