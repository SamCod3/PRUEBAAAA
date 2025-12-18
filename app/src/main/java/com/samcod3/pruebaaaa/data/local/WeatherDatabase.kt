package com.samcod3.pruebaaaa.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.samcod3.pruebaaaa.data.local.dao.FavoriteCityDao
import com.samcod3.pruebaaaa.data.local.dao.WeatherDao
import com.samcod3.pruebaaaa.data.local.entity.FavoriteCityEntity
import com.samcod3.pruebaaaa.data.local.entity.WeatherEntity

@Database(entities = [WeatherEntity::class, FavoriteCityEntity::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun favoriteDao(): FavoriteCityDao
}
