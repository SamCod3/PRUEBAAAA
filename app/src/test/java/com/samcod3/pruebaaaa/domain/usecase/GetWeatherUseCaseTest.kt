package com.samcod3.pruebaaaa.domain.usecase

import app.cash.turbine.test
import com.samcod3.pruebaaaa.domain.model.Weather
import com.samcod3.pruebaaaa.domain.repository.IWeatherRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetWeatherUseCaseTest {

    private lateinit var repository: IWeatherRepository
    private lateinit var useCase: GetWeatherUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetWeatherUseCase(repository)
    }

    @Test
    fun `invoke calls repository getWeather with correct municipioId`() = runTest {
        // Given
        val municipioId = "28079"
        val expectedWeather = createTestWeather(municipioId)
        every { repository.getWeather(municipioId) } returns flowOf(expectedWeather)

        // When
        useCase(municipioId).test {
            // Then
            assertEquals(expectedWeather, awaitItem())
            awaitComplete()
        }
        
        verify(exactly = 1) { repository.getWeather(municipioId) }
    }

    @Test
    fun `invoke returns null when repository returns null`() = runTest {
        // Given
        val municipioId = "99999"
        every { repository.getWeather(municipioId) } returns flowOf(null)

        // When
        useCase(municipioId).test {
            // Then
            assertEquals(null, awaitItem())
            awaitComplete()
        }
    }

    private fun createTestWeather(municipioId: String) = Weather(
        municipioId = municipioId,
        nombreMunicipio = "Madrid",
        provincia = "Madrid",
        lastUpdated = System.currentTimeMillis(),
        fecha = "2025-12-18",
        temperaturaActual = 20,
        temperaturaMax = 25,
        temperaturaMin = 10,
        descripcionCielo = "Despejado",
        probabilidadLluvia = 0,
        vientoVelocidad = 10,
        vientoDireccion = "N",
        hourlyForecast = emptyList(),
        dailyForecast = emptyList()
    )
}
