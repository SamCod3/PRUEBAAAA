package com.samcod3.pruebaaaa.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.samcod3.pruebaaaa.data.local.entity.FavoriteCityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCityDao {
    @Query("SELECT * FROM favorite_cities ORDER BY addedAt DESC")
    fun getFavorites(): Flow<List<FavoriteCityEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_cities WHERE id = :cityId)")
    suspend fun isFavorite(cityId: String): Boolean

    @Query("SELECT id FROM favorite_cities")
    suspend fun getFavoriteIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(city: FavoriteCityEntity)

    @Query("DELETE FROM favorite_cities WHERE id = :cityId")
    suspend fun deleteFavoriteById(cityId: String)
}
