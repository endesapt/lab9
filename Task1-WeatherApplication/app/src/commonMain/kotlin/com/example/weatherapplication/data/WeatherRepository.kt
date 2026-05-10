package com.example.weatherapplication.data

import com.example.weatherapplication.cache.CacheResult
import com.example.weatherapplication.cache.WeatherCache

data class WeatherResult(
    val weather: CurrentWeather,
    val fromCache: Boolean,
    val isStale: Boolean
)

class WeatherRepository(
    private val api: OpenMeteoApi,
    private val cache: WeatherCache
) {
    suspend fun loadCities(defaults: List<City>): List<City> {
        val cached = cache.loadCities()
        return if (cached.isNotEmpty()) {
            cached
        } else {
            cache.saveCities(defaults)
            defaults
        }
    }

    fun saveCities(cities: List<City>) {
        cache.saveCities(cities)
    }

    suspend fun searchCity(query: String): City? {
        return api.searchCity(query)
    }

    suspend fun fetchCurrent(city: City): WeatherResult {
        return try {
            val weather = api.fetchCurrent(city)
            cache.saveCurrent(city, weather)
            WeatherResult(weather = weather, fromCache = false, isStale = false)
        } catch (exception: Exception) {
            val cached: CacheResult? = cache.loadCurrent(city)
            if (cached != null) {
                WeatherResult(
                    weather = cached.weather,
                    fromCache = true,
                    isStale = cached.isStale
                )
            } else {
                throw exception
            }
        }
    }
}
