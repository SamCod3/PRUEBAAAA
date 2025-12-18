package com.samcod3.pruebaaaa.di

import android.content.Context
import androidx.room.Room
import com.samcod3.pruebaaaa.data.local.WeatherDatabase
import com.samcod3.pruebaaaa.data.local.dao.FavoriteCityDao
import com.samcod3.pruebaaaa.data.local.dao.WeatherDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWeatherDatabase(@ApplicationContext context: Context): WeatherDatabase {
        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideWeatherDao(database: WeatherDatabase): WeatherDao {
        return database.weatherDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteDao(database: WeatherDatabase): FavoriteCityDao {
        return database.favoriteDao()
    }
}
