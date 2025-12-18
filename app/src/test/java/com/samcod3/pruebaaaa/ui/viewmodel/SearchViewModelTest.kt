package com.samcod3.pruebaaaa.ui.viewmodel

import com.samcod3.pruebaaaa.domain.model.City
import com.samcod3.pruebaaaa.domain.usecase.SearchCityUseCase
import com.samcod3.pruebaaaa.domain.usecase.ToggleFavoriteCityUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): SearchViewModel {
        val searchCityUseCase: SearchCityUseCase = mockk(relaxed = true)
        val toggleFavoriteCityUseCase: ToggleFavoriteCityUseCase = mockk(relaxed = true)
        return SearchViewModel(searchCityUseCase, toggleFavoriteCityUseCase)
    }

    @Test
    fun `initial state is empty`() = runTest {
        // Given & When
        val viewModel = createViewModel()

        // Then
        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertEquals(emptyList<City>(), state.results)
        assertFalse(state.isLoading)
    }

    @Test
    fun `onQueryChange updates query in state`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onQueryChange("Mad")

        // Then
        assertEquals("Mad", viewModel.uiState.value.query)
    }

    @Test
    fun `clearing query clears results`() = runTest {
        // Given
        val searchResults = listOf(
            City(id = "28079", nombre = "Madrid", provincia = "Madrid", isFavorite = false)
        )
        val searchCityUseCase: SearchCityUseCase = mockk {
            coEvery { this@mockk.invoke(any()) } returns searchResults
        }
        val toggleFavoriteCityUseCase: ToggleFavoriteCityUseCase = mockk(relaxed = true)
        val viewModel = SearchViewModel(searchCityUseCase, toggleFavoriteCityUseCase)

        // When
        viewModel.onQueryChange("")

        // Then
        assertEquals(emptyList<City>(), viewModel.uiState.value.results)
    }

    @Test
    fun `toggleFavorite updates local state`() = runTest {
        // Given
        val city = City(id = "28079", nombre = "Madrid", provincia = "Madrid", isFavorite = false)
        val searchResults = listOf(city)
        val searchCityUseCase: SearchCityUseCase = mockk {
            coEvery { this@mockk.invoke(any()) } returns searchResults
        }
        val toggleFavoriteCityUseCase: ToggleFavoriteCityUseCase = mockk(relaxed = true)
        val viewModel = SearchViewModel(searchCityUseCase, toggleFavoriteCityUseCase)

        // Simular que tenemos resultados en el state
        viewModel.onQueryChange("Madrid")
        advanceTimeBy(600) // esperar debounce

        // When
        viewModel.toggleFavorite(city)

        // Then
        coVerify { toggleFavoriteCityUseCase(city) }
    }
}
