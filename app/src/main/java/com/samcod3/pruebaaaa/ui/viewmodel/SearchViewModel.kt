package com.samcod3.pruebaaaa.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samcod3.pruebaaaa.domain.model.City
import com.samcod3.pruebaaaa.domain.usecase.SearchCityUseCase
import com.samcod3.pruebaaaa.domain.usecase.ToggleFavoriteCityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<City> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchCityUseCase: SearchCityUseCase,
    private val toggleFavoriteCityUseCase: ToggleFavoriteCityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        setupSearchSubscription()
    }

    @OptIn(FlowPreview::class)
    private fun setupSearchSubscription() {
        _searchQuery
            .debounce(500L)
            .distinctUntilChanged()
            .filter { it.length >= 3 }
            .onEach { query ->
                performSearch(query)
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
        _uiState.value = _uiState.value.copy(query = newQuery)
        
        if (newQuery.isBlank()) {
            _uiState.value = _uiState.value.copy(results = emptyList())
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            android.util.Log.d("SearchViewModel", "Buscando: $query")
            val results = searchCityUseCase(query)
            android.util.Log.d("SearchViewModel", "Resultados: ${results.size}")
            _uiState.value = _uiState.value.copy(
                results = results,
                isLoading = false
            )
        }
    }

    fun toggleFavorite(city: City) {
        viewModelScope.launch {
            toggleFavoriteCityUseCase(city)
            // Actualizamos la lista localmente para reflejar el cambio inmediato
            val updatedList = _uiState.value.results.map {
                if (it.id == city.id) it.copy(isFavorite = !it.isFavorite) else it
            }
            _uiState.value = _uiState.value.copy(results = updatedList)
        }
    }
}
