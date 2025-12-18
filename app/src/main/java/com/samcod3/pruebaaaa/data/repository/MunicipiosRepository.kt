package com.samcod3.pruebaaaa.data.repository

import android.util.Log
import com.samcod3.pruebaaaa.data.remote.AemetApi
import com.samcod3.pruebaaaa.data.remote.model.MunicipioDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Normalizer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para obtener la lista de municipios desde la API de AEMET.
 * Los municipios se cachean en memoria tras la primera carga.
 */
@Singleton
class MunicipiosRepository @Inject constructor(
    private val api: AemetApi
) {
    private var municipiosCache: List<MunicipioDto>? = null

    /**
     * Obtiene la lista completa de municipios desde la API de AEMET.
     * La lista se cachea en memoria para no repetir llamadas.
     * Si falla, NO cachea para permitir reintentos.
     */
    suspend fun getMunicipios(): List<MunicipioDto> {
        return withContext(Dispatchers.IO) {
            // Si ya tenemos cache con datos, devolverlo
            municipiosCache?.let { 
                if (it.isNotEmpty()) return@withContext it 
            }
            
            try {
                Log.d("MunicipiosRepository", "Cargando municipios desde AEMET API...")
                val response = api.getMunicipiosUrl()
                
                if (response.estado == 200) {
                    val lista = api.getMunicipiosList(response.datos)
                    if (lista.isNotEmpty()) {
                        municipiosCache = lista
                        Log.d("MunicipiosRepository", "Cargados ${lista.size} municipios")
                    }
                    lista
                } else {
                    Log.e("MunicipiosRepository", "Error AEMET: ${response.descripcion}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("MunicipiosRepository", "Error cargando municipios", e)
                emptyList()
            }
        }
    }

    /**
     * Busca el código de municipio por nombre (para geolocalización).
     */
    suspend fun getCodeForLocation(nombreMunicipio: String): String? {
        val lista = getMunicipios()
        val nombreBusqueda = normalize(nombreMunicipio)
        
        return lista.firstOrNull { 
            normalize(it.nombre) == nombreBusqueda || normalize(it.nombre).contains(nombreBusqueda)
        }?.getCleanId()
    }

    /**
     * Busca municipios por nombre (para el buscador).
     * Devuelve máximo 30 resultados ordenados por relevancia.
     */
    suspend fun searchMunicipios(query: String): List<MunicipioDto> {
        if (query.isBlank() || query.length < 2) return emptyList()
        
        val lista = getMunicipios()
        val normalizedQuery = normalize(query)
        
        return lista
            .filter { normalize(it.nombre).contains(normalizedQuery) }
            .sortedWith(compareBy(
                // Priorizar coincidencias exactas al inicio
                { !normalize(it.nombre).startsWith(normalizedQuery) },
                // Luego por longitud del nombre (más cortos primero)
                { it.nombre.length }
            ))
            .take(30)
    }

    private fun normalize(input: String): String {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase()
            .trim()
    }
}
