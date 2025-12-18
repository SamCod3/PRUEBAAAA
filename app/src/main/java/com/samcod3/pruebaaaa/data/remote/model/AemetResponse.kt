package com.samcod3.pruebaaaa.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AemetResponse(
    @Json(name = "descripcion") val descripcion: String?,
    @Json(name = "estado") val estado: Int,
    @Json(name = "datos") val datos: String, // La URL donde están los datos reales
    @Json(name = "metadatos") val metadatos: String?
)
