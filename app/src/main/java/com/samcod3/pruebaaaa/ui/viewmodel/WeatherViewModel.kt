package com.samcod3.pruebaaaa.ui.viewmodel

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samcod3.pruebaaaa.data.location.LocationTracker
import com.samcod3.pruebaaaa.data.repository.ApiKeyRepository
import com.samcod3.pruebaaaa.data.repository.MunicipiosRepository
import com.samcod3.pruebaaaa.domain.model.City
import com.samcod3.pruebaaaa.domain.model.Weather
import com.samcod3.pruebaaaa.domain.usecase.GetWeatherUseCase
import com.samcod3.pruebaaaa.domain.usecase.IsFavoriteUseCase
import com.samcod3.pruebaaaa.domain.usecase.RefreshWeatherUseCase
import com.samcod3.pruebaaaa.domain.usecase.ToggleFavoriteCityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Success(val weather: Weather) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val refreshWeatherUseCase: RefreshWeatherUseCase,
    private val toggleFavoriteCityUseCase: ToggleFavoriteCityUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val locationTracker: LocationTracker,
    private val municipiosRepository: MunicipiosRepository,
    private val apiKeyRepository: ApiKeyRepository,
    private val application: Application,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _currentMunicipioId = MutableStateFlow("28079")
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _showApiKeyDialog = MutableStateFlow(false)
    val showApiKeyDialog: StateFlow<Boolean> = _showApiKeyDialog.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    // Intervalo mínimo para volver a pedir datos a la API (15 minutos)
    private val REFRESH_THRESHOLD_MS = 15 * 60 * 1000L

    val uiState: StateFlow<WeatherUiState> = combine(
        _currentMunicipioId.flatMapLatest { id ->
            // Cada vez que cambia el ID, comprobamos si es favorito
            checkIfFavorite(id)
            getWeatherUseCase(id) 
        },
        _isLoading,
        _errorMessage
    ) { weather, isLoading, error ->
        when {
            weather != null -> WeatherUiState.Success(weather)
            isLoading -> WeatherUiState.Loading
            error != null -> WeatherUiState.Error(error)
            else -> WeatherUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WeatherUiState.Loading
    )

    init {
        checkApiKey()
    }

    private fun checkIfFavorite(id: String) {
        viewModelScope.launch {
            _isFavorite.value = isFavoriteUseCase(id)
        }
    }

    fun toggleFavorite() {
        val currentState = uiState.value
        if (currentState is WeatherUiState.Success) {
            viewModelScope.launch {
                val weather = currentState.weather
                val city = City(
                    id = weather.municipioId,
                    nombre = weather.nombreMunicipio,
                    provincia = weather.provincia,
                    isFavorite = _isFavorite.value
                )
                toggleFavoriteCityUseCase(city)
                _isFavorite.value = !_isFavorite.value
            }
        }
    }

    private fun checkApiKey() {
        if (!apiKeyRepository.hasApiKey()) {
            _showApiKeyDialog.value = true
        } else {
            // Verificamos si venimos con un ID de navegación
            val navArgId = savedStateHandle.get<String>("municipioId")
            if (!navArgId.isNullOrBlank() && navArgId != "current_location") {
                _currentMunicipioId.value = navArgId
                refreshData()
            } else {
                // Si no hay argumento o es "current_location", usamos geolocalización
                // Nota: Esto solo inicia si ya tenemos API Key, si no esperamos al guardado
                 fetchLocationAndWeather()
            }
        }
    }

    fun saveApiKey(key: String) {
        apiKeyRepository.setApiKey(key)
        _showApiKeyDialog.value = false
        // Una vez guardada, verificamos navegación de nuevo
        val navArgId = savedStateHandle.get<String>("municipioId")
        if (!navArgId.isNullOrBlank() && navArgId != "current_location") {
             _currentMunicipioId.value = navArgId
             refreshData()
        } else {
            fetchLocationAndWeather()
        }
    }

    fun openApiKeyDialog() {
        _showApiKeyDialog.value = true
    }

    fun closeApiKeyDialog() {
        _showApiKeyDialog.value = false
        // Si cerramos sin guardar y no hay key, se quedará en estado vacío o error, pero el usuario puede volver a abrirlo
    }

    fun checkRefreshOnResume() {
        val currentState = uiState.value
        if (currentState is WeatherUiState.Success) {
            val lastUpdated = currentState.weather.lastUpdated
            val now = System.currentTimeMillis()
            if (now - lastUpdated > REFRESH_THRESHOLD_MS) {
                // Usar refreshData para respetar el municipio actual
                refreshData()
            }
        } else if (currentState is WeatherUiState.Error) {
            // Si hay error, refrescar con el municipio actual (o geolocalizar si no hay ninguno)
            if (_currentMunicipioId.value.isNotBlank()) {
                refreshData()
            } else {
                fetchLocationAndWeather()
            }
        }
    }

    fun fetchLocationAndWeather() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val location = locationTracker.getCurrentLocation()
            if (location != null) {
                try {
                    val geocoder = Geocoder(application, Locale.getDefault())
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        // Intentamos obtener el nombre del municipio de forma más robusta
                        val cityName = address.locality ?: address.subAdminArea ?: address.featureName
                        
                        if (cityName != null) {
                            // 1. Buscamos el ID de AEMET por el nombre del municipio obtenido del Geocoder
                            val aemetId = municipiosRepository.getCodeForLocation(cityName)
                            
                            if (aemetId != null) {
                                _currentMunicipioId.value = aemetId
                            } else {
                                // 2. Fallback: Si no encontramos por nombre, usamos el CP (tu lógica antigua)
                                val cp = address.postalCode
                                val fallbackAemetCode = when {
                                    cp == "14520" -> "14027" // Fernán-Núñez (Excepción manual)
                                    cp?.startsWith("14") == true -> "14021" // Córdoba
                                    cp?.startsWith("28") == true -> "28079" // Madrid
                                    cp?.startsWith("08") == true -> "08019" // Barcelona
                                    cp?.startsWith("41") == true -> "41091" // Sevilla
                                    cp?.startsWith("46") == true -> "46250" // Valencia
                                    else -> "28079" // Fallback final a Madrid
                                }
                                _currentMunicipioId.value = fallbackAemetCode
                                // Podríamos mostrar un mensaje si usamos fallback: _errorMessage.value = "Ubicación no reconocida, mostrando datos de ${if(fallbackAemetCode == "28079") "Madrid" else "otra ciudad"}"
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Fallo Geocoder o búsqueda, mantenemos el municipio actual o Madrid por defecto
                    // Podríamos loggear el error: Log.e("WeatherViewModel", "Location/Geocoding error", e)
                }
            }

            // 2. Refrescar datos del tiempo para el municipio (nuevo o el que ya teníamos)
            refreshData()
            _isLoading.value = false
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            // _isLoading.value = true // Ya se maneja en fetchLocationAndWeather
            val result = refreshWeatherUseCase(_currentMunicipioId.value)
            
            result.onFailure {
                // Si falla la red, guardamos el mensaje de error.
                // La UI solo lo mostrará si NO tenemos datos en caché (ver bloque combine arriba)
                _errorMessage.value = it.message ?: "Error de conexión"
            }
            // _isLoading.value = false // Ya se maneja en fetchLocationAndWeather
        }
    }
}


