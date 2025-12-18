package com.samcod3.pruebaaaa.domain.usecase

import com.samcod3.pruebaaaa.domain.model.City
import com.samcod3.pruebaaaa.domain.repository.ICityRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SearchCityUseCaseTest {

    private lateinit var repository: ICityRepository
    private lateinit var useCase: SearchCityUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = SearchCityUseCase(repository)
    }

    @Test
    fun `invoke returns list of cities matching query`() = runTest {
        // Given
        val query = "Madrid"
        val expectedCities = listOf(
            City(id = "28079", nombre = "Madrid", provincia = "Madrid", isFavorite = false),
            City(id = "28115", nombre = "Alcalá de Henares", provincia = "Madrid", isFavorite = true)
        )
        coEvery { repository.searchCity(query) } returns expectedCities

        // When
        val result = useCase(query)

        // Then
        assertEquals(expectedCities, result)
        coVerify(exactly = 1) { repository.searchCity(query) }
    }

    @Test
    fun `invoke returns empty list when no cities match`() = runTest {
        // Given
        val query = "NoExiste123"
        coEvery { repository.searchCity(query) } returns emptyList()

        // When
        val result = useCase(query)

        // Then
        assertEquals(emptyList<City>(), result)
    }
}
