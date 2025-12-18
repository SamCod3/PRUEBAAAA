package com.samcod3.pruebaaaa.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_cities")
data class FavoriteCityEntity(
    @PrimaryKey
    val id: String,
    val nombre: String,
    val provincia: String,
    val addedAt: Long = System.currentTimeMillis()
)
