package com.samcod3.pruebaaaa.data.mapper.domain

import com.samcod3.pruebaaaa.data.local.entity.WeatherEntity
import com.samcod3.pruebaaaa.domain.model.Weather

fun WeatherEntity.toDomain(): Weather {
    return Weather(
        municipioId = municipioId,
        nombreMunicipio = nombreMunicipio,
        provincia = provincia,
        lastUpdated = lastUpdated,
        fecha = fecha,
        temperaturaActual = temperaturaActual,
        temperaturaMax = temperaturaMax,
        temperaturaMin = temperaturaMin,
        descripcionCielo = descripcionCielo,
        probabilidadLluvia = probabilidadLluvia,
        vientoVelocidad = vientoVelocidad,
        vientoDireccion = vientoDireccion ?: "",
        hourlyForecast = hourlyForecast,
        dailyForecast = dailyForecast
    )
}
