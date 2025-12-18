package com.samcod3.pruebaaaa.data.remote

import com.samcod3.pruebaaaa.data.remote.model.AemetResponse
import com.samcod3.pruebaaaa.data.remote.model.MunicipioDto
import com.samcod3.pruebaaaa.data.remote.model.TownForecastDto
import com.samcod3.pruebaaaa.data.remote.model.DailyForecastDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface AemetApi {

    // 1. Obtener la URL de los datos horarios para un municipio específico
    @GET("api/prediccion/especifica/municipio/horaria/{municipioCode}")
    suspend fun getTownForecastUrl(@Path("municipioCode") municipioCode: String): AemetResponse

    // 2. Obtener los datos horarios desde la URL
    @GET
    suspend fun getForecastData(@Url url: String): List<TownForecastDto>

    // 3. Obtener la URL de los datos diarios para un municipio (incluye viento)
    @GET("api/prediccion/especifica/municipio/diaria/{municipioCode}")
    suspend fun getDailyForecastUrl(@Path("municipioCode") municipioCode: String): AemetResponse

    // 4. Obtener los datos diarios desde la URL
    @GET
    suspend fun getDailyForecastData(@Url url: String): List<DailyForecastDto>

    // 5. Obtener el maestro de municipios (lista de todas las localidades)
    @GET("api/maestro/municipios")
    suspend fun getMunicipiosUrl(): AemetResponse

    // 6. Obtener la lista de municipios desde la URL
    @GET
    suspend fun getMunicipiosList(@Url url: String): List<MunicipioDto>
}
