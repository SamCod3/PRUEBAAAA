package com.samcod3.pruebaaaa.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DTO para la predicción diaria de AEMET (incluye viento)
 */
@JsonClass(generateAdapter = true)
data class DailyForecastDto(
    @Json(name = "origen") val origen: Map<String, String>?,
    @Json(name = "elaborado") val elaborado: String?,
    @Json(name = "nombre") val nombre: String,
    @Json(name = "provincia") val provincia: String,
    @Json(name = "prediccion") val prediccion: PrediccionDiariaDto
)

@JsonClass(generateAdapter = true)
data class PrediccionDiariaDto(
    @Json(name = "dia") val dia: List<DiaDiarioDto>
)

@JsonClass(generateAdapter = true)
data class DiaDiarioDto(
    @Json(name = "fecha") val fecha: String,
    @Json(name = "probPrecipitacion") val probPrecipitacion: List<ProbPrecipitacionDiariaDto>?,
    @Json(name = "cotaNieveProv") val cotaNieveProv: List<CotaNieveDto>?,
    @Json(name = "estadoCielo") val estadoCielo: List<EstadoCieloDiarioDto>?,
    @Json(name = "viento") val viento: List<VientoDiarioDto>?,
    @Json(name = "rachaMax") val rachaMax: List<RachaMaxDto>?,
    @Json(name = "temperatura") val temperatura: TemperaturaDiariaDto?,
    @Json(name = "sensTermica") val sensTermica: TemperaturaDiariaDto?,
    @Json(name = "humedadRelativa") val humedadRelativa: HumedadDiariaDto?,
    @Json(name = "uvMax") val uvMax: Int?
)

@JsonClass(generateAdapter = true)
data class ProbPrecipitacionDiariaDto(
    @Json(name = "value") val value: Int?,
    @Json(name = "periodo") val periodo: String?
)

@JsonClass(generateAdapter = true)
data class CotaNieveDto(
    @Json(name = "value") val value: String?,
    @Json(name = "periodo") val periodo: String?
)

@JsonClass(generateAdapter = true)
data class EstadoCieloDiarioDto(
    @Json(name = "value") val value: String?,
    @Json(name = "periodo") val periodo: String?,
    @Json(name = "descripcion") val descripcion: String?
)

@JsonClass(generateAdapter = true)
data class VientoDiarioDto(
    @Json(name = "direccion") val direccion: String?,
    @Json(name = "velocidad") val velocidad: Int?,
    @Json(name = "periodo") val periodo: String?
)

@JsonClass(generateAdapter = true)
data class RachaMaxDto(
    @Json(name = "value") val value: String?,
    @Json(name = "periodo") val periodo: String?
)

@JsonClass(generateAdapter = true)
data class TemperaturaDiariaDto(
    @Json(name = "maxima") val maxima: Int?,
    @Json(name = "minima") val minima: Int?
)

@JsonClass(generateAdapter = true)
data class HumedadDiariaDto(
    @Json(name = "maxima") val maxima: Int?,
    @Json(name = "minima") val minima: Int?
)
