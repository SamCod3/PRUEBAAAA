package com.samcod3.pruebaaaa.di

import com.samcod3.pruebaaaa.data.repository.CityRepositoryImpl
import com.samcod3.pruebaaaa.data.repository.WeatherRepository
import com.samcod3.pruebaaaa.domain.repository.ICityRepository
import com.samcod3.pruebaaaa.domain.repository.IWeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepository: WeatherRepository
    ): IWeatherRepository

    @Binds
    @Singleton
    abstract fun bindCityRepository(
        cityRepository: CityRepositoryImpl
    ): ICityRepository
}
