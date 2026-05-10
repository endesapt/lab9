package com.example.weatherapplication

import com.example.weatherapplication.cache.CacheStore
import com.example.weatherapplication.cache.WeatherCache
import com.example.weatherapplication.data.City
import com.example.weatherapplication.data.OpenMeteoApi
import com.example.weatherapplication.data.WeatherRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WeatherRepositoryIntegrationTest {
    @Test
    fun searchCityReturnsCity() = runTest {
        val repo = WeatherRepository(OpenMeteoApi(buildClient(successEngine())), WeatherCache(TestCacheStore()))
        val city = repo.searchCity("Minsk")
        assertNotNull(city)
        assertEquals("Minsk", city.name)
    }

    @Test
    fun fetchCurrentCachesResult() = runTest {
        val store = TestCacheStore()
        val repo = WeatherRepository(OpenMeteoApi(buildClient(successEngine())), WeatherCache(store))
        val city = City(name = "Minsk", country = "Belarus", latitude = 53.9, longitude = 27.56)
        val result = repo.fetchCurrent(city)
        assertEquals(20, result.weather.temperatureC)
        val cached = WeatherCache(store).loadCurrent(city)
        assertNotNull(cached)
    }

    @Test
    fun fetchCurrentFallsBackToCache() = runTest {
        val store = TestCacheStore()
        val cache = WeatherCache(store)
        val city = City(name = "Minsk", country = "Belarus", latitude = 53.9, longitude = 27.56)
        val repo = WeatherRepository(OpenMeteoApi(buildClient(successEngine())), cache)
        repo.fetchCurrent(city)

        val failingRepo = WeatherRepository(OpenMeteoApi(buildClient(failingEngine())), cache)
        val result = failingRepo.fetchCurrent(city)
        assertTrue(result.fromCache)
    }
}

private class TestCacheStore : CacheStore {
    private val data = mutableMapOf<String, String>()

    override fun read(key: String): String? = data[key]

    override fun write(key: String, value: String) {
        data[key] = value
    }

    override fun remove(key: String) {
        data.remove(key)
    }
}

private fun buildClient(engine: MockEngine): HttpClient {
    return HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
}

private fun successEngine(): MockEngine {
    return MockEngine { request ->
        val path = request.url.encodedPath
        val payload = if (path.contains("/v1/search")) GEO_RESPONSE else FORECAST_RESPONSE
        respond(
            content = payload,
            headers = headersOf("Content-Type", "application/json")
        )
    }
}

private fun failingEngine(): MockEngine {
    return MockEngine {
        respondError(HttpStatusCode.ServiceUnavailable)
    }
}

private const val GEO_RESPONSE = """
{
  "results": [
    {
      "name": "Minsk",
      "country": "Belarus",
      "latitude": 53.9,
      "longitude": 27.56
    }
  ]
}
"""

private const val FORECAST_RESPONSE = """
{
  "current": {
    "temperature_2m": 20.0,
    "relative_humidity_2m": 45.0,
    "weather_code": 2,
    "wind_speed_10m": 5.0
  }
}
"""
