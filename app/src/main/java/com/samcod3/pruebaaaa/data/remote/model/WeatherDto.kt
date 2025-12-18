package com.samcod3.pruebaaaa.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TownForecastDto(
    @Json(name = "origen") val origen: Map<String, String>?,
    @Json(name = "elaborado") val elaborado: String?,
    @Json(name = "nombre") val nombre: String,
    @Json(name = "provincia") val provincia: String,
    @Json(name = "prediccion") val prediccion: PrediccionDto
)

@JsonClass(generateAdapter = true)
data class PrediccionDto(
    @Json(name = "dia") val dia: List<DiaDto>
)

@JsonClass(generateAdapter = true)
data class DiaDto(
    @Json(name = "fecha") val fecha: String, // Formato YYYY-MM-DD
    @Json(name = "probPrecipitacion") val probPrecipitacion: List<PeriodoValueDto>?,
    @Json(name = "precipitacion") val precipitacion: List<PeriodoValueDto>?, // mm de lluvia por hora
    @Json(name = "estadoCielo") val estadoCielo: List<EstadoCieloDto>?,
    @Json(name = "viento") val viento: List<VientoDto>?,
    // En la predicción horaria, temperatura es una lista de valores por hora
    @Json(name = "temperatura") val temperatura: List<PeriodoValueDto>?,
    @Json(name = "sensTermica") val sensTermica: List<PeriodoValueDto>?,
    @Json(name = "humedadRelativa") val humedadRelativa: List<PeriodoValueDto>?
)

@JsonClass(generateAdapter = true)
data class PeriodoValueDto(
    @Json(name = "value") val value: String?,
    @Json(name = "periodo") val periodo: String?
)

@JsonClass(generateAdapter = true)
data class EstadoCieloDto(
    @Json(name = "value") val value: String?,
    @Json(name = "periodo") val periodo: String?,
    @Json(name = "descripcion") val descripcion: String?
)

@JsonClass(generateAdapter = true)
data class VientoDto(
    @Json(name = "direccion") val direccion: String?, // Dirección cardinal: N, NE, S, SW, etc.
    @Json(name = "velocidad") val velocidad: String?, // Velocidad en km/h
    @Json(name = "periodo") val periodo: String?
)
