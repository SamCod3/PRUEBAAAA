package com.samcod3.pruebaaaa.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samcod3.pruebaaaa.domain.model.City
import com.samcod3.pruebaaaa.domain.usecase.GetFavoriteCitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    getFavoriteCitiesUseCase: GetFavoriteCitiesUseCase
) : ViewModel() {

    val favorites: StateFlow<List<City>> = getFavoriteCitiesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
