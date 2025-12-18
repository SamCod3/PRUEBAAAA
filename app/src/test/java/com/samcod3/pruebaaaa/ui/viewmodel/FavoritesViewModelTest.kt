package com.samcod3.pruebaaaa.ui.viewmodel

import app.cash.turbine.test
import com.samcod3.pruebaaaa.domain.model.City
import com.samcod3.pruebaaaa.domain.usecase.GetFavoriteCitiesUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `favorites emits list from use case when collected`() = runTest {
        // Given
        val favoriteCities = listOf(
            City(id = "28079", nombre = "Madrid", provincia = "Madrid", isFavorite = true),
            City(id = "08019", nombre = "Barcelona", provincia = "Barcelona", isFavorite = true)
        )
        val citiesFlow = MutableStateFlow(favoriteCities)
        val getFavoriteCitiesUseCase: GetFavoriteCitiesUseCase = mockk {
            every { this@mockk.invoke() } returns citiesFlow
        }

        // When
        val viewModel = FavoritesViewModel(getFavoriteCitiesUseCase)

        // Then - Usar Turbine para colectar el StateFlow
        viewModel.favorites.test {
            // El primer valor podría ser emptyList (initialValue) o los datos
            val emission = awaitItem()
            // Si es lista vacía, espera la siguiente emisión
            if (emission.isEmpty()) {
                assertEquals(favoriteCities, awaitItem())
            } else {
                assertEquals(favoriteCities, emission)
            }
        }
    }

    @Test
    fun `favorites starts with empty list as initial value`() = runTest {
        // Given
        val citiesFlow = MutableStateFlow<List<City>>(emptyList())
        val getFavoriteCitiesUseCase: GetFavoriteCitiesUseCase = mockk {
            every { this@mockk.invoke() } returns citiesFlow
        }

        // When
        val viewModel = FavoritesViewModel(getFavoriteCitiesUseCase)

        // Then
        viewModel.favorites.test {
            assertEquals(emptyList<City>(), awaitItem())
        }
    }
}
