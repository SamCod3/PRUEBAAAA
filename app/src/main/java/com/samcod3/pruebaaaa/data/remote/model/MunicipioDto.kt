package com.samcod3.pruebaaaa.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DTO para el municipio devuelto por el endpoint /api/maestro/municipios
 * Ejemplo: {"nombre": "Madrid", "id": "id28079", ...}
 */
@JsonClass(generateAdapter = true)
data class MunicipioDto(
    @Json(name = "nombre") val nombre: String,
    @Json(name = "id") val id: String,
    @Json(name = "id_old") val idOld: String? = null,
    @Json(name = "url") val url: String? = null,
    @Json(name = "latitud") val latitud: String? = null,
    @Json(name = "longitud") val longitud: String? = null,
    @Json(name = "latitud_dec") val latitudDec: String? = null,
    @Json(name = "longitud_dec") val longitudDec: String? = null,
    @Json(name = "altitud") val altitud: String? = null,
    @Json(name = "num_hab") val numHab: String? = null,
    @Json(name = "zona_comarcal") val zonaComarcal: String? = null,
    @Json(name = "destacado") val destacado: String? = null,
    @Json(name = "capital") val capital: String? = null
) {
    /**
     * Extrae el código de municipio sin el prefijo "id"
     * Ej: "id28079" -> "28079"
     */
    fun getCleanId(): String = id.removePrefix("id")
    
    /**
     * Extrae la provincia del código (primeros 2 dígitos)
     */
    fun getProvinciaName(): String {
        val code = getCleanId().take(2)
        return provinciaCodes[code] ?: "Desconocida"
    }
    
    companion object {
        private val provinciaCodes = mapOf(
            "01" to "Álava", "02" to "Albacete", "03" to "Alicante", "04" to "Almería",
            "05" to "Ávila", "06" to "Badajoz", "07" to "Illes Balears", "08" to "Barcelona",
            "09" to "Burgos", "10" to "Cáceres", "11" to "Cádiz", "12" to "Castellón",
            "13" to "Ciudad Real", "14" to "Córdoba", "15" to "A Coruña", "16" to "Cuenca",
            "17" to "Girona", "18" to "Granada", "19" to "Guadalajara", "20" to "Gipuzkoa",
            "21" to "Huelva", "22" to "Huesca", "23" to "Jaén", "24" to "León",
            "25" to "Lleida", "26" to "La Rioja", "27" to "Lugo", "28" to "Madrid",
            "29" to "Málaga", "30" to "Murcia", "31" to "Navarra", "32" to "Ourense",
            "33" to "Asturias", "34" to "Palencia", "35" to "Las Palmas", "36" to "Pontevedra",
            "37" to "Salamanca", "38" to "Santa Cruz de Tenerife", "39" to "Cantabria",
            "40" to "Segovia", "41" to "Sevilla", "42" to "Soria", "43" to "Tarragona",
            "44" to "Teruel", "45" to "Toledo", "46" to "Valencia", "47" to "Valladolid",
            "48" to "Bizkaia", "49" to "Zamora", "50" to "Zaragoza", "51" to "Ceuta",
            "52" to "Melilla"
        )
    }
}
